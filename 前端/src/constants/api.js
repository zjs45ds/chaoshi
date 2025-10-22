/**
 * API服务常量和类型定义
 */

// API端点常量
export const API_ENDPOINTS = {
  // 用户相关
  USER: {
    LOGIN: '/auth/login',
    REGISTER: '/auth/register',
    LOGOUT: '/auth/logout',
    PROFILE: '/user/profile',
    UPDATE_PROFILE: '/user/profile',
    CHANGE_PASSWORD: '/user/password',
    UPLOAD_AVATAR: '/user/avatar',
    GET_FAVORITES: '/user/favorites',
    ADD_FAVORITE: '/user/favorites',
    REMOVE_FAVORITE: '/user/favorites'
  },

  // 音乐相关
  MUSIC: {
    SEARCH: '/search',
    HOT_SONGS: '/songs/hot',
    SONG_DETAIL: '/songs',
    SONG_URL: '/songs/url',
    SONG_LYRIC: '/songs/lyric',
    DAILY_RECOMMEND: '/recommend/daily',
    PERSONAL_FM: '/radio/personal-fm'
  },

  // 歌手相关
  ARTIST: {
    LIST: '/artists',
    DETAIL: '/artists',
    HOT_SONGS: '/artists/songs',
    ALBUMS: '/artists/albums',
    MV: '/artists/mv',
    FOLLOW: '/artists/follow',
    UNFOLLOW: '/artists/unfollow'
  },

  // 专辑相关
  ALBUM: {
    LIST: '/albums',
    DETAIL: '/albums',
    HOT: '/albums/hot',
    NEW: '/albums/new',
    SONGS: '/albums/songs'
  },

  // 歌单相关
  PLAYLIST: {
    LIST: '/playlists',
    DETAIL: '/playlists',
    HOT: '/playlists/hot',
    CATEGORY: '/playlists/category',
    CREATE: '/playlists',
    UPDATE: '/playlists',
    DELETE: '/playlists',
    SUBSCRIBE: '/playlists/subscribe',
    UNSUBSCRIBE: '/playlists/unsubscribe',
    TRACKS: '/playlists/tracks'
  },

  // 排行榜相关
  TOPLIST: {
    LIST: '/toplists',
    DETAIL: '/toplists'
  },

  // MV相关
  MV: {
    LIST: '/mv',
    DETAIL: '/mv',
    HOT: '/mv/hot',
    NEW: '/mv/new',
    URL: '/mv/url'
  },


  // 上传相关
  UPLOAD: {
    IMAGE: '/upload/image',
    AUDIO: '/upload/audio'
  }
}

// 错误码常量
export const ERROR_CODES = {
  SUCCESS: 200,
  BAD_REQUEST: 400,
  UNAUTHORIZED: 401,
  FORBIDDEN: 403,
  NOT_FOUND: 404,
  INTERNAL_ERROR: 500,
  SERVICE_UNAVAILABLE: 503
}

// 业务状态码
export const BUSINESS_CODES = {
  SUCCESS: 0,
  PARAM_ERROR: 1001,
  USER_NOT_EXISTS: 1002,
  PASSWORD_ERROR: 1003,
  TOKEN_INVALID: 1004,
  TOKEN_EXPIRED: 1005,
  PERMISSION_DENIED: 1006,
  RESOURCE_NOT_FOUND: 1007,
  RESOURCE_ALREADY_EXISTS: 1008,
  OPERATION_FAILED: 1009,
  RATE_LIMIT_EXCEEDED: 1010
}

// 请求方法类型
export const REQUEST_METHODS = {
  GET: 'GET',
  POST: 'POST',
  PUT: 'PUT',
  DELETE: 'DELETE',
  PATCH: 'PATCH'
}

// 内容类型
export const CONTENT_TYPES = {
  JSON: 'application/json',
  FORM_DATA: 'multipart/form-data',
  FORM_URLENCODED: 'application/x-www-form-urlencoded'
}

// 搜索类型
export const SEARCH_TYPES = {
  SONG: 'song',
  ARTIST: 'artist', 
  ALBUM: 'album',
  PLAYLIST: 'playlist',
  MV: 'mv',
  USER: 'user',
  LYRIC: 'lyric'
}

// 音质类型
export const QUALITY_TYPES = {
  STANDARD: 'standard',
  HIGH: 'high', 
  LOSSLESS: 'lossless',
  HIRES: 'hires'
}

// 播放模式
export const PLAY_MODES = {
  SEQUENCE: 'sequence',
  LOOP: 'loop',
  SHUFFLE: 'shuffle',
  SINGLE: 'single'
}


// 文件大小限制
export const FILE_SIZE_LIMITS = {
  AVATAR: 2 * 1024 * 1024, // 2MB
  BACKGROUND: 10 * 1024 * 1024, // 10MB
  AUDIO: 50 * 1024 * 1024 // 50MB
}

// 支持的文件格式
export const SUPPORTED_FORMATS = {
  IMAGE: ['jpg', 'jpeg', 'png', 'gif', 'webp'],
  AUDIO: ['mp3', 'flac', 'ape', 'wav', 'aac', 'ogg']
}

// 分页默认配置
export const PAGINATION = {
  DEFAULT_PAGE: 1,
  DEFAULT_SIZE: 20,
  MAX_SIZE: 100
}

// 缓存配置
export const CACHE_CONFIG = {
  USER_INFO: 30 * 60 * 1000, // 30分钟
  SONG_LIST: 5 * 60 * 1000,  // 5分钟
  HOT_DATA: 60 * 60 * 1000,  // 1小时
  SEARCH_RESULT: 2 * 60 * 1000 // 2分钟
}

// 本地存储键名
export const STORAGE_KEYS = {
  TOKEN: 'token',
  USER_INFO: 'userInfo',
  THEME: 'theme',
  PLAY_HISTORY: 'playHistory',
  FAVORITES: 'favorites',
  SETTINGS: 'settings',
  LAST_PLAYLIST: 'lastPlaylist'
}

// 事件名称
export const EVENT_NAMES = {
  USER_LOGIN: 'user-login',
  USER_LOGOUT: 'user-logout',
  THEME_CHANGE: 'theme-change',
  PLAY_SONG: 'play-song',
  PLAYLIST_CHANGE: 'playlist-change',
  BACKGROUND_CHANGE: 'background-change'
}

export default {
  API_ENDPOINTS,
  ERROR_CODES,
  BUSINESS_CODES,
  REQUEST_METHODS,
  CONTENT_TYPES,
  SEARCH_TYPES,
  QUALITY_TYPES,
  PLAY_MODES,
  PLAYLIST_CATEGORIES,
  USER_LEVELS,
  FILE_SIZE_LIMITS,
  SUPPORTED_FORMATS,
  PAGINATION,
  CACHE_CONFIG,
  STORAGE_KEYS,
  EVENT_NAMES
}