package domain

type Pattern struct {
	ID          int64  `json:"id"`
	Name        string `json:"name"`
	Description string `json:"description"`
	Example     string `json:"example"`
	Tasks       []Task `json:"tasks,omitempty"`
}
