import React, { Component, PropTypes } from 'react';
import { connect } from 'dva';
import { Link } from 'dva/router';

import styles from './Welcome.less';
var classNames = require('classnames');

const propTypes = {
};

class Welcome extends Component {

	handleTestClick(e) {
		BootstrapDialog.show({
            message: '创建任务成功，按任意键返回任务列表页面。'
        });
  	}

	render(){
		return (
	  	<div>
			<div className={styles.banner}>
		        <div className={classNames(styles.container1,"container")}>
		            <h1>ETL: Extract, Transform, Load</h1>
		            <p>数据抽取、转换、加载管理工具</p>
		        </div>
		    </div>
		    <div className={styles.middle}>
		        <div className={classNames(styles.container2,"container")}>
		            <div className="col-md-9">
		                <h2>使用ETL Admin控制面板可以管理定时任务的触发任务。</h2>
		                <p>可以查看定时任务和触发任务的每次执行信息，以及每次执行所产生的详细日志信息。</p>
		                <div className={styles.loginPanel}>
		                    <p><strong>登录进入控制面板:</strong></p>
		                    <a href="#" className="btn btn-success" onClick={this.handleTestClick}>控制面板</a>
		                </div>
		            </div>
		            <div className="col-md-3">
		                <h2>快捷菜单</h2>
		                <ul className="nav nav-pills nav-stacked">
		                    <li><a id="foo" href="/" target="_blank">定时器任务</a></li>
		                    <li><a href="/" target="_blank">触发器任务</a></li>
		                    <li><a href="#" target="_blank">查看执行</a></li>
		                    <li><a href="#" target="_blank">查看执行日志</a></li>
		                </ul>
		            </div>
		        </div>
		    </div>
		</div>
	  	);
	}
}

Welcome.propTypes = {
};

export default Welcome;
