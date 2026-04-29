package app

import (
	"context"
	"fmt"
	"net/http"

	"RoadmapMicro/internal/config"
	"RoadmapMicro/internal/db"
	deliveryhttp "RoadmapMicro/internal/delivery/http"
	"RoadmapMicro/internal/delivery/http/handler"
	"RoadmapMicro/internal/repository"
	"RoadmapMicro/internal/usecase"
)

type App struct {
	server *http.Server
	db     interface {
		Close()
	}
}

func New(ctx context.Context, cfg config.Config) (*App, error) {
	postgresPool, err := db.NewPostgresPool(ctx, cfg.DatabaseURL())
	if err != nil {
		return nil, err
	}

	roadmapRepository := repository.NewPostgresRoadmapRepository(postgresPool)
	roadmapUsecase := usecase.NewRoadmapUsecase(roadmapRepository)

	healthHandler := handler.NewHealthHandler()
	roadmapHandler := handler.NewRoadmapHandler(roadmapUsecase)

	router := deliveryhttp.NewRouter(deliveryhttp.RouterDependencies{
		HealthHandler:  healthHandler,
		RoadmapHandler: roadmapHandler,
		JWTSecret:      cfg.JWTSecret,
	})

	server := &http.Server{
		Addr:    ":" + cfg.AppPort,
		Handler: router,
	}

	return &App{
		server: server,
		db:     postgresPool,
	}, nil
}

func (a *App) Run() error {
	fmt.Printf("Roadmap service started on http://localhost%s\n", a.server.Addr)

	return a.server.ListenAndServe()
}

func (a *App) Close() {
	if a.db != nil {
		a.db.Close()
	}
}
