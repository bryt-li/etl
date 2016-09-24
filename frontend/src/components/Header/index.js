import React from 'react'
import {
  Row,
  Col,
  Icon,
  Menu,
  Dropdown
} from 'antd'
import './index.less'
import {
  Link
} from 'react-router'

const SubMenu = Menu.SubMenu;
const MenuItemGroup = Menu.ItemGroup;

export default class Header extends React.Component {
  constructor() {
    super()
  }

  handleClick() {

  }

  render() {
    const {
      user
    } = this.props
    return (
      <div className='ant-layout-header'>
        <Menu className="header-menu" onClick={this.handleClick}
        mode="horizontal">
          <SubMenu title={<span><Icon type="user" />{user.user}</span>}>
            <Menu.Item key="setting:1">帐户设置</Menu.Item>
            <Menu.Item key="setting:2">锁定界面</Menu.Item>
            <Menu.Divider />
            <Menu.Item key="setting:3">注销</Menu.Item>
          </SubMenu>
          <Menu.Item key="mail">
            <Icon type="question" />帮助
          </Menu.Item>
        </Menu>
      </div>
    )
  }
}