import React, { Component, PropTypes } from 'react';
import { connect } from 'dva';
import { Link } from 'dva/router';

import MainLayout from '../components/layout/MainLayout.js'

import styles from './App.less';

function App(props) {
  return (
    <MainLayout user={props.user} allowRegister={props.allowRegister} 
    			menuItems={props.menuItems}>
      {props.children}
    </MainLayout>
  );
}

App.propTypes = {
};


/*
const mapStateToProps = (state) => {
  const {user, allowRegister, menuItems} = state.app;

  return {
      user: user,
      allowRegister: allowRegister,
      menuItems: menuItems
  };
};
*/

const mapStateToProps = (state) => ({
	user: state.app.user,
	allowRegister: state.app.allowRegister,
	menuItems: state.app.menuItems
});

export default connect(mapStateToProps)(App);
