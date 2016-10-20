
export default {

  namespace: 'app',

  state: {
  	user: null,
  	allowRegister: true,
  	menuItems: [
  		{
  			icon: 'home',
  			name: '欢迎',
  			children: [{name:'111'},{name:'222'},{name:'333'}]
  		},
  		{
  			icon: 'windows',
  			name: 'Windows',
  			children: [{name:'111'},{name:'222'},{name:'333'}]
  		}
  	]
  },

  reducers: {
  },

}
