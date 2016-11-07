import React, { PropTypes } from 'react';
import { Router, Route, IndexRoute, Link } from 'dva/router';

import Container from './routes/Container';
import Welcome from './routes/Welcome';
import Login from './routes/Login';

import Dashboard from './routes/Dashboard';


export default function({ history }) {
  return (
    <Router history={history}>
		<Route path="/" component={Container}>
			<IndexRoute breadcrumbName="欢迎首页" component={Welcome} />
	    	<Route path="login" breadcrumbName="登录" component={Login} />

	    	<Route path="dashboard" breadcrumbName="控制面板" component={Dashboard} />
	    </Route>
	</Router>
	);
};
