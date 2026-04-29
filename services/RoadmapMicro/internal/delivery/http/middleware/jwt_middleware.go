package middleware

import (
	"context"
	"encoding/json"
	"errors"
	"net/http"
	"strings"

	"github.com/golang-jwt/jwt/v5"
	"github.com/google/uuid"
)

type contextKey string

const accountIDContextKey contextKey = "accountID"

type JWTMiddleware struct {
	secret []byte
}

type AccessTokenClaims struct {
	AccountID string `json:"accountId"`
	jwt.RegisteredClaims
}

func NewJWTMiddleware(secret string) *JWTMiddleware {
	return &JWTMiddleware{
		secret: []byte(secret),
	}
}

func (m *JWTMiddleware) RequireAuth(next http.Handler) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		tokenRaw, err := extractBearerToken(r)
		if err != nil {
			writeAuthError(w, "missing or invalid authorization header")
			return
		}

		accountID, err := m.validateToken(tokenRaw)
		if err != nil {
			writeAuthError(w, "invalid or expired token")
			return
		}

		ctx := context.WithValue(r.Context(), accountIDContextKey, accountID)

		next.ServeHTTP(w, r.WithContext(ctx))
	})
}

func AccountIDFromContext(ctx context.Context) (string, bool) {
	accountID, ok := ctx.Value(accountIDContextKey).(string)
	if !ok || accountID == "" {
		return "", false
	}

	return accountID, true
}

func extractBearerToken(r *http.Request) (string, error) {
	header := r.Header.Get("Authorization")
	if header == "" {
		return "", errors.New("authorization header is empty")
	}

	const bearerPrefix = "Bearer "

	if !strings.HasPrefix(header, bearerPrefix) {
		return "", errors.New("authorization header must start with Bearer")
	}

	token := strings.TrimSpace(strings.TrimPrefix(header, bearerPrefix))
	if token == "" {
		return "", errors.New("bearer token is empty")
	}

	return token, nil
}

func (m *JWTMiddleware) validateToken(tokenRaw string) (string, error) {
	claims := &AccessTokenClaims{}

	token, err := jwt.ParseWithClaims(tokenRaw, claims, func(token *jwt.Token) (any, error) {
		if token.Method == nil || token.Method.Alg() != jwt.SigningMethodHS256.Alg() {
			return nil, errors.New("unexpected signing method")
		}

		return m.secret, nil
	})
	if err != nil {
		return "", err
	}

	if !token.Valid {
		return "", errors.New("token is not valid")
	}

	if claims.AccountID == "" {
		return "", errors.New("accountId is empty")
	}

	if _, err := uuid.Parse(claims.AccountID); err != nil {
		return "", errors.New("accountId is not valid uuid")
	}

	return claims.AccountID, nil
}

func writeAuthError(w http.ResponseWriter, message string) {
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusUnauthorized)

	_ = json.NewEncoder(w).Encode(map[string]string{
		"error": message,
	})
}
