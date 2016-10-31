import React, { Component, PropTypes } from 'react';
import { connect } from 'dva';
import { Link } from 'dva/router';

import styles from './Login.less';
var classNames = require('classnames');

import { Spin, Modal } from 'antd';

import { Form, Input, Button, Checkbox } from 'antd';
const FormItem = Form.Item;


function noop() {
  return false;
}


//https://github.com/dvajs/dva-knowledgemap#react-component
class Login extends Component {

	constructor(props) {
		super(props);
		this.state = {account: '', password: ''};
	}

	//Using React.createClass will automatically bind this values correctly for us, but changes when using ES6 classes affect this.
	//Ref: https://toddmotto.com/react-create-class-versus-component/

	handleAccountChange = (e) => {
		this.setState({account: e.target.value});
	}

	handlePasswordChange = (e) => {
		this.setState({password: e.target.value});
	}

	handleReset = (e) => {
		e.preventDefault();
		this.props.form.resetFields();
	}

	handleSubmit = (e) => {
		e.preventDefault();
	    console.log('Received values of form:', this.props.form.getFieldsValue());
	    this.props.form.validateFields((errors, values) => {
	    	if (errors) {
	    		console.log('Errors in form!!!');
	    		return;
	    	}
	    	console.log('Submit!!!');
	    	console.log(values);
	    });
	    return;

		var account = this.state.account.trim();
		var password = this.state.password.trim();

		if (!account || !password) {
			Modal.error({
				title: '错误',
				content: '用户名或密码输入格式不正确，请检查。',
			});
			return;
		}

    	// dispatch login
    	this.props.dispatch({
    		type: 'app/login', 
    		payload:{account:account,password:password} 
    	});

    	//this.setState({name: '', password: ''});
    }

	render(){

		const errorCls = classNames({
			"text-warning": true,
			"hide": !this.props.error || this.props.loading,
		});

		const formItemLayout = {
			labelCol: { span: 7 },
			wrapperCol: { span: 12 },
		};

	    const { getFieldDecorator, getFieldError, isFieldValidating } = this.props.form;

		const container = (
			<div className={styles.loginFormContainer}>
				<Form horizontal onSubmit={this.handleSubmit}>
					<FormItem
			          label="用户名"
			          hasFeedback
			          help={isFieldValidating('account') ? 'validating...' : (getFieldError('account') || []).join(', ')}
			        >
			          {getFieldDecorator('account', {
			            rules: [
			              { required: true, min: 4, message: '用户名最少4个字符' },
			            ],
			          })(
			            <Input placeholder="请输入用户名" />
			          )}
			        </FormItem>

			        <FormItem
			          label="密码"
			          hasFeedback>
			          {getFieldDecorator('passwordd', {
			            rules: [
			              { required: true, whitespace: true, message: '请输入登录密码' },
			            ],
			          })(
			            <Input type="password" autoComplete="off"
			              onContextMenu={noop} onPaste={noop} onCopy={noop} onCut={noop}
			            />
			          )}
			        </FormItem>
			        <FormItem>
			          {getFieldDecorator('remember')(
			            <Checkbox>记住账号和密码，下次自动登录</Checkbox>
			          )}
			        </FormItem>
					<h4 className={errorCls}>{this.props.error}</h4>

			        <Button type="primary" htmlType="submit">登录系统</Button>
			     </Form>
		     </div>
		);

		return (
			<Spin size="large" spinning={this.props.loading}>{container}</Spin>
	  	);
	}
}


Login.propTypes = {
};

Login = Form.create({})(Login);

const mapStateToProps = (state) => ({
	loading: state.loading.global,
	error: state.app.error
});

export default connect(mapStateToProps)(Login);
