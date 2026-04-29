package usecase

import (
	"context"

	"RoadmapMicro/internal/domain"
	"RoadmapMicro/internal/repository"
)

type RoadmapUsecase struct {
	roadmapRepository repository.RoadmapRepository
}

func NewRoadmapUsecase(roadmapRepository repository.RoadmapRepository) *RoadmapUsecase {
	return &RoadmapUsecase{
		roadmapRepository: roadmapRepository,
	}
}

func (u *RoadmapUsecase) GetAllPatterns(ctx context.Context, accountID string) ([]domain.Pattern, error) {
	return u.roadmapRepository.GetAllPatterns(ctx, accountID)
}

func (u *RoadmapUsecase) GetPatternByID(ctx context.Context, patternID int64, accountID string) (*domain.Pattern, error) {
	return u.roadmapRepository.GetPatternByID(ctx, patternID, accountID)
}

func (u *RoadmapUsecase) GetTasksByPatternID(ctx context.Context, patternID int64, accountID string) ([]domain.Task, error) {
	return u.roadmapRepository.GetTasksByPatternID(ctx, patternID, accountID)
}
