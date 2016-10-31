import React, { Component, PropTypes } from 'react';
import { connect } from 'dva';
import { Link } from 'dva/router';

import styles from './Container.less';

import Header from '../components/Header';
import Footer from '../components/Footer';


import { Menu, Breadcrumb, Icon } from 'antd';
const SubMenu = Menu.SubMenu;
const MenuItemGroup = Menu.ItemGroup;

class Container extends Component {

  constructor(props) {
    super(props);
    this.state = {collapse: false, currentNavMenu: 'mail'};
  }

  onCollapseChange = (e) => {
    this.setState({
      collapse: !this.state.collapse,
    });
  }

  handleNavMenuClick = (e) => {
    console.log('click ', e);
    this.setState({
      currentNavMenu: e.key,
    });
  }

  render() {
    const collapse = this.state.collapse;
    return (
    <div className={collapse ? "ant-layout-aside ant-layout-aside-collapse" : "ant-layout-aside"}>

      <aside className="ant-layout-sider">
        <div className="ant-layout-logo">ETL</div>
        <Menu mode="inline" theme="light" defaultSelectedKeys={['user']}>
          <Menu.Item key="user">
            <Icon type="user" /><span className="nav-text">导航一</span>
          </Menu.Item>
          <Menu.Item key="setting">
            <Icon type="setting" /><span className="nav-text">导航二</span>
          </Menu.Item>
          <Menu.Item key="laptop">
            <Icon type="laptop" /><span className="nav-text">导航三</span>
          </Menu.Item>
          <Menu.Item key="notification">
            <Icon type="notification" /><span className="nav-text">导航四</span>
          </Menu.Item>
          <Menu.Item key="folder">
            <Icon type="folder" /><span className="nav-text">导航五</span>
          </Menu.Item>
        </Menu>
        <div className="ant-aside-action" onClick={this.onCollapseChange}>
          {collapse ? <Icon type="right" /> : <Icon type="left" />}
        </div>
      </aside>


      <div className="ant-layout-main">
        <div className="ant-layout-header">

          <div className="ant-layout-breadcrumb">
            <Breadcrumb {...this.props} />
          </div>
  
          <Menu onClick={this.handleNavMenuClick}
            selectedKeys={[this.state.currentNavMenu]}
            mode="horizontal">
            <Menu.Item key="mail">
              <Icon type="mail" />首页
            </Menu.Item>
            <Menu.Item key="app">
              <Icon type="appstore" />控制面板
            </Menu.Item>
            <Menu.Item key="alipay">
              <a href="#" target="_blank" rel="noopener noreferrer">帮助中心</a>
            </Menu.Item>
            <SubMenu title={<span><Icon type="setting" />用户登录</span>}>
                <Menu.Item key="setting:1">账户信息</Menu.Item>
                <Menu.Item key="setting:2">退出登录</Menu.Item>
            </SubMenu>
          </Menu>
        </div>
        <div className="ant-layout-container">
          <div className="ant-layout-content">
            <div style={{ height: '100%' }}>
              {this.props.children}
            </div>
          </div>
        </div>

        <div className="ant-layout-footer">
        Mr-V.cn 版权所有 © 2016 由威创客技术部支持
        </div>
      </div>
    </div>


    );
  }
}

Container.propTypes = {
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
  role: state.app.role,
	allowRegister: state.app.allowRegister,
	menuItems: state.app.menuItems
});

export default connect(mapStateToProps)(Container);
