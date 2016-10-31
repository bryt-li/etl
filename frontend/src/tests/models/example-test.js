import expect from 'expect';
import example from '../../models/example';

describe('example', () => {

	describe('reducer', () => {

		describe('fetch', () => {
			it('should be the same', () => {
				expect(example.reducers['fetch']({}, { payload: { a: 1 }})).toEqual({ a: 1 });
			});  		
		})

		describe('foo', () => {
			it('should be the same', () => {
				expect(example.reducers['foo']({}, { payload: { a: 1 }})).toEqual({ a: 1 });
			});  		
		})


	})
});
