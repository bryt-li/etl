import React, { Component } from 'react';
import {
  render,
} from 'react-dom';
import {
  Router,
  Route,
  IndexRoute,
  hashHistory,
} from 'react-router';

import {
  Topbar,
  Nav,
  CollapsibleNav,
} from 'amazeui-react';

import RouteLink from './components/RouteLink';
import SiteFooter from './components/SiteFooter';

class App extends Component {
  render() {
    return (
      <div className="ask-page">
        <Topbar
          className="ask-header"
          brand="Amaze UI"
          brandLink="/"
          inverse
        >
          <CollapsibleNav>
            <Nav topbar>
              <RouteLink to="page1">页面 1</RouteLink>
              <RouteLink to="page2">页面 2</RouteLink>
            </Nav>
          </CollapsibleNav>
        </Topbar>
        <main className="ask-main">
          {this.props.children}
        </main>
        <SiteFooter />
      </div>
    );
  }
}

// Pages
import Index from './pages/Index';
import Page1 from './pages/Page1';
import Page2 from './pages/Page2';

const routes = (
  <Router history={hashHistory}>
    <Route path="/" component={App}>
      <IndexRoute component={Index} />
      <Route path="page1" component={Page1} />
      <Route path="page2" component={Page2} />
    </Route>
  </Router>
);

document.addEventListener('DOMContentLoaded', () => {
  render(routes, document.getElementById('root'));
});
