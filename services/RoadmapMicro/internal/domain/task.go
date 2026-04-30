package domain

type Task struct {
	ID          int64  `json:"id"`
	Name        string `json:"name"`
	Description string `json:"description,omitempty"`
	Number      int    `json:"number"`
	PatternID   int64  `json:"patternId"`
	IsSolved    bool   `json:"isSolved"`
}
