-- 创建音乐状态枚举类型
CREATE TYPE music_status AS ENUM ('PENDING', 'GENERATING', 'COMPLETED', 'FAILED', 'CANCELLED');

-- 创建用户表
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 创建音乐表
CREATE TABLE IF NOT EXISTS music (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id),
    prompt TEXT NOT NULL,
    generation_id VARCHAR(100),
    audio_url VARCHAR(255),
    lyrics TEXT,
    duration INTEGER,
    style VARCHAR(50),
    status music_status NOT NULL DEFAULT 'PENDING',
    error_message TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
); 