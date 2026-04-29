package http

import (
	"net/http"

	"RoadmapMicro/internal/delivery/http/handler"
	"RoadmapMicro/internal/delivery/http/middleware"
)

type RouterDependencies struct {
	HealthHandler  *handler.HealthHandler
	RoadmapHandler *handler.RoadmapHandler
	JWTSecret      string
}

func NewRouter(deps RouterDependencies) http.Handler {
	mux := http.NewServeMux()

	jwtMiddleware := middleware.NewJWTMiddleware(deps.JWTSecret)

	mux.HandleFunc("GET /health", deps.HealthHandler.Health)

	mux.Handle(
		"GET /roadmap/v1/patterns",
		jwtMiddleware.RequireAuth(http.HandlerFunc(deps.RoadmapHandler.GetAllPatterns)),
	)

	mux.Handle(
		"GET /roadmap/v1/patterns/{id}",
		jwtMiddleware.RequireAuth(http.HandlerFunc(deps.RoadmapHandler.GetPatternByID)),
	)

	mux.Handle(
		"GET /roadmap/v1/patterns/{id}/tasks",
		jwtMiddleware.RequireAuth(http.HandlerFunc(deps.RoadmapHandler.GetTasksByPatternID)),
	)

	return mux
}
