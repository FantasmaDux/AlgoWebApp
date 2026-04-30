package handler

import (
	"encoding/json"
	"errors"
	"net/http"
	"strconv"

	"RoadmapMicro/internal/delivery/http/middleware"
	"RoadmapMicro/internal/repository"
	"RoadmapMicro/internal/usecase"
)

type RoadmapHandler struct {
	roadmapUsecase *usecase.RoadmapUsecase
}

func NewRoadmapHandler(roadmapUsecase *usecase.RoadmapUsecase) *RoadmapHandler {
	return &RoadmapHandler{
		roadmapUsecase: roadmapUsecase,
	}
}

func (h *RoadmapHandler) GetAllPatterns(w http.ResponseWriter, r *http.Request) {
	accountID, ok := middleware.AccountIDFromContext(r.Context())
	if !ok {
		writeError(w, http.StatusUnauthorized, "accountId not found in request context")
		return
	}

	patterns, err := h.roadmapUsecase.GetAllPatterns(r.Context(), accountID)
	if err != nil {
		writeError(w, http.StatusInternalServerError, "failed to get patterns")
		return
	}

	writeJSON(w, http.StatusOK, patterns)
}

func (h *RoadmapHandler) GetPatternByID(w http.ResponseWriter, r *http.Request) {
	idRaw := r.PathValue("id")

	id, err := strconv.ParseInt(idRaw, 10, 64)
	if err != nil {
		writeError(w, http.StatusBadRequest, "invalid pattern id")
		return
	}

	accountID, ok := middleware.AccountIDFromContext(r.Context())
	if !ok {
		writeError(w, http.StatusUnauthorized, "accountId not found in request context")
		return
	}

	pattern, err := h.roadmapUsecase.GetPatternByID(r.Context(), id, accountID)
	if err != nil {
		if errors.Is(err, repository.ErrPatternNotFound) {
			writeError(w, http.StatusNotFound, "pattern not found")
			return
		}

		writeError(w, http.StatusInternalServerError, "failed to get pattern")
		return
	}

	writeJSON(w, http.StatusOK, pattern)
}

func (h *RoadmapHandler) GetTasksByPatternID(w http.ResponseWriter, r *http.Request) {
	idRaw := r.PathValue("id")

	id, err := strconv.ParseInt(idRaw, 10, 64)
	if err != nil {
		writeError(w, http.StatusBadRequest, "invalid pattern id")
		return
	}

	accountID, ok := middleware.AccountIDFromContext(r.Context())
	if !ok {
		writeError(w, http.StatusUnauthorized, "accountId not found in request context")
		return
	}

	tasks, err := h.roadmapUsecase.GetTasksByPatternID(r.Context(), id, accountID)
	if err != nil {
		if errors.Is(err, repository.ErrPatternNotFound) {
			writeError(w, http.StatusNotFound, "pattern not found")
			return
		}

		writeError(w, http.StatusInternalServerError, "failed to get tasks")
		return
	}

	writeJSON(w, http.StatusOK, tasks)
}

func writeJSON(w http.ResponseWriter, statusCode int, data any) {
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(statusCode)

	_ = json.NewEncoder(w).Encode(data)
}

func writeError(w http.ResponseWriter, statusCode int, message string) {
	writeJSON(w, statusCode, map[string]string{
		"error": message,
	})
}
