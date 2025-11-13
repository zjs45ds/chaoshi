// 假用户数据
export default {
  id: 1,
  nickname: '长大ing',
  avatar: 'https://q1.qlogo.cn/g?b=qq&nk=10000&s=100',
  follow: 7,
  fans: 3,
  bgImg: 'https://img.zcool.cn/community/01b6c95d5b2e5fa801216518b8e1e7.jpg',
  tabs: [
    { name: '我的喜欢', key: 'like' },
    { name: '我创建的歌单', key: 'playlist' }
  ],
  songs: [
    { id: 1, name: '念', artist: '薛之谦', album: '守村人', duration: '05:08', mv: true },
    { id: 2, name: '丑八怪', artist: '薛之谦', album: '绅士', duration: '03:26', mv: false },
    { id: 3, name: '演员', artist: '薛之谦', album: '绅士', duration: '04:52', mv: true },
    { id: 4, name: '你还要我怎样', artist: '薛之谦', album: '意外', duration: '05:12', mv: false }
  ],
}