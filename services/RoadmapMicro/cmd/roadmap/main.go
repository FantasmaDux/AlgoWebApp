package main

import (
	"context"
	"log"

	"RoadmapMicro/internal/app"
	"RoadmapMicro/internal/config"
)

func main() {
	ctx := context.Background()

	cfg, err := config.Load()
	if err != nil {
		log.Fatalf("failed to load config: %v", err)
	}

	application, err := app.New(ctx, cfg)
	if err != nil {
		log.Fatalf("failed to create app: %v", err)
	}
	defer application.Close()

	if err := application.Run(); err != nil {
		log.Fatal(err)
	}
}
