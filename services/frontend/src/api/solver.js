import axios from 'axios';

const API_GATEWAY = 'http://localhost:8080';

const solverApi = axios.create({
  baseURL: API_GATEWAY,
});

solverApi.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// ========== ПАТТЕРНЫ ==========
export const getPatterns = () => solverApi.get('/roadmap/v1/patterns');

// ========== ЗАДАЧИ ==========
export const getTask = (taskId) => {
  return solverApi.get(`/solver/v1/tasks/${taskId}`);
};

export const getTasksByPattern = (patternId) => {
  return solverApi.get(`/roadmap/v1/patterns/${patternId}/tasks`);
};

// ========== РЕШЕНИЯ ==========
export const submitSolution = (taskId, code, language) => {
  return solverApi.post('/solver/v1/submit', {
    taskId,
    code,
    language
  });
};

export const getSolvedTasks = () => {
  return solverApi.get('/solver/v1/users/me/solved-tasks');
};

export const isTaskSolved = (taskId) => {
  return solverApi.get(`/solver/v1/tasks/${taskId}/solved`);
};

export default solverApi;