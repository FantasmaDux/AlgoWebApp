package config

import (
	"fmt"
	"os"

	"github.com/joho/godotenv"
)

type Config struct {
	AppPort string

	DBHost     string
	DBPort     string
	DBUser     string
	DBPassword string
	DBName     string
	DBSSLMode  string

	JWTSecret string
}

func Load() (Config, error) {
	_ = godotenv.Load()

	cfg := Config{
		AppPort: getEnv("APP_PORT", "8082"),

		DBHost:     os.Getenv("DB_HOST"),
		DBPort:     getEnv("DB_PORT", "5432"),
		DBUser:     os.Getenv("DB_USER"),
		DBPassword: os.Getenv("DB_PASSWORD"),
		DBName:     os.Getenv("DB_NAME"),
		DBSSLMode:  getEnv("DB_SSLMODE", "disable"),

		JWTSecret: os.Getenv("JWT_SECRET"),
	}

	if err := cfg.validate(); err != nil {
		return Config{}, err
	}

	return cfg, nil
}

func (c Config) DatabaseURL() string {
	return fmt.Sprintf(
		"postgres://%s:%s@%s:%s/%s?sslmode=%s",
		c.DBUser,
		c.DBPassword,
		c.DBHost,
		c.DBPort,
		c.DBName,
		c.DBSSLMode,
	)
}

func (c Config) validate() error {
	required := map[string]string{
		"DB_HOST":     c.DBHost,
		"DB_USER":     c.DBUser,
		"DB_PASSWORD": c.DBPassword,
		"DB_NAME":     c.DBName,
		"JWT_SECRET":  c.JWTSecret,
	}

	for key, value := range required {
		if value == "" {
			return fmt.Errorf("required env variable %s is not set", key)
		}
	}

	return nil
}

func getEnv(key string, defaultValue string) string {
	value := os.Getenv(key)
	if value == "" {
		return defaultValue
	}

	return value
}
