package repository

import (
	"context"
	"errors"
	"fmt"

	"RoadmapMicro/internal/domain"

	"github.com/jackc/pgx/v5"
	"github.com/jackc/pgx/v5/pgxpool"
)

type PostgresRoadmapRepository struct {
	db *pgxpool.Pool
}

func NewPostgresRoadmapRepository(db *pgxpool.Pool) *PostgresRoadmapRepository {
	return &PostgresRoadmapRepository{
		db: db,
	}
}

func (r *PostgresRoadmapRepository) GetAllPatterns(ctx context.Context, accountID string) ([]domain.Pattern, error) {
	rows, err := r.db.Query(ctx, `
		SELECT id, name, description, example
		FROM patterns
		ORDER BY id
	`)
	if err != nil {
		return nil, fmt.Errorf("query patterns: %w", err)
	}
	defer rows.Close()

	patterns := make([]domain.Pattern, 0)

	for rows.Next() {
		var pattern domain.Pattern

		if err := rows.Scan(
			&pattern.ID,
			&pattern.Name,
			&pattern.Description,
			&pattern.Example,
		); err != nil {
			return nil, fmt.Errorf("scan pattern: %w", err)
		}

		tasks, err := r.GetTasksByPatternID(ctx, pattern.ID, accountID)
		if err != nil {
			return nil, fmt.Errorf("get tasks for pattern %d: %w", pattern.ID, err)
		}

		pattern.Tasks = tasks
		patterns = append(patterns, pattern)
	}

	if err := rows.Err(); err != nil {
		return nil, fmt.Errorf("iterate patterns: %w", err)
	}

	return patterns, nil
}

func (r *PostgresRoadmapRepository) GetPatternByID(ctx context.Context, patternID int64, accountID string) (*domain.Pattern, error) {
	var pattern domain.Pattern

	err := r.db.QueryRow(ctx, `
		SELECT id, name, description, example
		FROM patterns
		WHERE id = $1
	`, patternID).Scan(
		&pattern.ID,
		&pattern.Name,
		&pattern.Description,
		&pattern.Example,
	)

	if err != nil {
		if errors.Is(err, pgx.ErrNoRows) {
			return nil, ErrPatternNotFound
		}

		return nil, fmt.Errorf("query pattern by id: %w", err)
	}

	tasks, err := r.GetTasksByPatternID(ctx, pattern.ID, accountID)
	if err != nil {
		return nil, fmt.Errorf("get tasks for pattern %d: %w", pattern.ID, err)
	}

	pattern.Tasks = tasks

	return &pattern, nil
}

func (r *PostgresRoadmapRepository) GetTasksByPatternID(ctx context.Context, patternID int64, accountID string) ([]domain.Task, error) {
	rows, err := r.db.Query(ctx, `
		SELECT
			t.id,
			t.name,
			t.description,
			t.number,
			t.pattern_id,
			EXISTS (
				SELECT 1
				FROM solution s
				WHERE s.task_id = t.id
				  AND s.user_id = $1::uuid
				  AND LOWER(s.status) IN ('solved', 'accepted', 'success', 'true')
			) AS is_solved
		FROM tasks t
		WHERE t.pattern_id = $2
		ORDER BY t.number
	`, accountID, patternID)
	if err != nil {
		return nil, fmt.Errorf("query tasks by pattern id: %w", err)
	}
	defer rows.Close()

	tasks := make([]domain.Task, 0)

	for rows.Next() {
		var task domain.Task

		if err := rows.Scan(
			&task.ID,
			&task.Name,
			&task.Description,
			&task.Number,
			&task.PatternID,
			&task.IsSolved,
		); err != nil {
			return nil, fmt.Errorf("scan task: %w", err)
		}

		tasks = append(tasks, task)
	}

	if err := rows.Err(); err != nil {
		return nil, fmt.Errorf("iterate tasks: %w", err)
	}

	return tasks, nil
}
