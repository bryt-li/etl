import pathToRegexp from 'path-to-regexp';
import { routerRedux } from 'dva/router';

import * as service from "../services/user";


export default {

  namespace: 'app',

  state: {
    id: null,
  	name: null,
    role: null,
    error: null,



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


  subscriptions: {
    
    /*user authentication*/
    accessControl({ dispatch, history }) {
      return history.listen(({ pathname }) => {
        dispatch({ type: 'checkLogin', payload: {pathname}});
      });
    },

  },


  effects: {

    *checkLogin({payload}, { select, put }){
      
      //Notice: selecting state with namespace, IMPORTANT!!!
      const id = yield select(state => state.app.id);
      const {pathname} = payload;

      //console.log(`check login - path: ${pathname} user: ${user}`);

      if(pathname == "/login" && id){
        yield put(routerRedux.replace('/etl'));
      }else{
        const match = pathToRegexp(`/etl/`).exec(pathname);
        if (match && !id) {
          yield put(routerRedux.push('/login'));
        }
      }
    },

    *login({payload}, { call, put }) {

      const { response, err } = yield call(service.login, payload);
      if(err || !response){
        yield put({type:'loginFailed',payload:err.message});
      }else if(response.ok) {
        console.log(`login success: ${response.data.id} ${response.data.name} ${response.data.role}`);
        yield put({
          type: 'loginSuccess',
          payload: response.data,
        });
        yield put(routerRedux.push('/etl'));
      }else{
        yield put({type:'loginFailed',payload:response.err});
      }
    },

    *logout({payload}, { call, put }){
      const { response, err } = yield call(service.logout);

      if (response && response.ok) {
        console.log(`logout success.`);

        yield put({type: 'logoutSuccess'});
        yield put(routerRedux.push('/'));
      }
    },

  },

  reducers: {
    loginSuccess(state, { payload }) {
      //console.log(`reducer - loginSuccess: ${payload.user} ${payload.role}`);
      return { ...state, id: payload.id, name: payload.name, role: payload.role };
    },

    loginFailed(state, {payload}){
      return { ...state, id: null, name: null, role: null, error: payload };
    },

    logoutSuccess(state, {payload}){
      //console.log(`reducer - logoutSuccess: ${state.user}`);
      return {...state, id: null, name: null, role: null };
    }

  },

}
