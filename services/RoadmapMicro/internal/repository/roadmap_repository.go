package repository

import (
	"context"
	"errors"

	"RoadmapMicro/internal/domain"
)

var ErrPatternNotFound = errors.New("pattern not found")

type RoadmapRepository interface {
	GetAllPatterns(ctx context.Context, accountID string) ([]domain.Pattern, error)
	GetPatternByID(ctx context.Context, patternID int64, accountID string) (*domain.Pattern, error)
	GetTasksByPatternID(ctx context.Context, patternID int64, accountID string) ([]domain.Task, error)
}
