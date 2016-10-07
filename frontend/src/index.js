import './index.html';
import './index.less';
import dva from 'dva';

// 1. Initialize
const app = dva();
/*
const app = dva({
	initialState: {
    	app: {

    	}
	}
});
*/

// 2. Plugins
//app.use({});

// 3. Model
app.model(require('./models/app'));

// 4. Router
app.router(require('./router'));

// 5. Start
app.start('#root');
