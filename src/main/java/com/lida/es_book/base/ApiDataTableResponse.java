package com.lida.es_book.base;

/*
 * dataTables响应
 * dataTables要求数据格式
 *Created by LidaDu on 2018/2/27.  
 */
public class ApiDataTableResponse extends ApiResponse{
    private int draw;//用于验证结果
    private long recordsTotal;//总数
    private long recordsFiltered;//用于分页

    public ApiDataTableResponse(ApiResponse.Status status) {
        this(status.getCode(), status.getStandardMessage(), null);
    }

    public ApiDataTableResponse(int code, String message, Object data) {
        super(code, message, data);
    }

    public int getDraw() {
        return draw;
    }

    public void setDraw(int draw) {
        this.draw = draw;
    }

    public long getRecordsTotal() {
        return recordsTotal;
    }

    public void setRecordsTotal(long recordsTotal) {
        this.recordsTotal = recordsTotal;
    }

    public long getRecordsFiltered() {
        return recordsFiltered;
    }

    public void setRecordsFiltered(long recordsFiltered) {
        this.recordsFiltered = recordsFiltered;
    }
}
