package net.zz.zjf.plugin.exception;


public class CommonException extends RuntimeException {
	private static final long serialVersionUID = 3230630383646562969L;

	private Integer code;

	public CommonException(Integer code) {
		this.code = code;
	}
	
	public CommonException(Integer code, String message) {
		super(message);
		this.code = code;
	}

	public Integer getCode() {
		return code;
	}


	

}
