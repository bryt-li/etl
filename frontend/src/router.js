import React, { PropTypes } from 'react';
import { Router, Route, IndexRoute, Link } from 'dva/router';

import App from './routes/App'
import Welcome from './routes/Welcome';
import IndexPage from './routes/IndexPage';

export default function({ history }) {
  return (
    <Router history={history}>
    	<Route path="/" component={App}>
    		<IndexRoute breadcrumbName="首页" component={Welcome} />
	    	<Route path="dashboard" breadcrumbName="仪表盘" component={IndexPage} />
	    </Route>
    </Router>
  );
};
