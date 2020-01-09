package cn.tedu.store.service.ex;

/**
 * 使用购物车id查询购物车记录，
 * 未查到时抛出的异常
 */
public class CartNotFoundException extends ServiceException {

	private static final long serialVersionUID = 1L;

	public CartNotFoundException() {
		super();
	}

	public CartNotFoundException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public CartNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public CartNotFoundException(String message) {
		super(message);
	}

	public CartNotFoundException(Throwable cause) {
		super(cause);
	}

}
