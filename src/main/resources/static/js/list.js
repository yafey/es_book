$(function () {
    var table = $("#bookTable").DataTable({
        "order": [[3, "desc"]],//默认创建时间排序
        "pageLength": 10, // 配置单页显示条数
        "paging": true, // 关闭本地分页
        "lengthChange": false, // 不允许用户改变表格每页显示的记录数
        "searching": false, // 不允许Datatables开启本地搜索
        "ordering": true, // 启用Datatables排序
        "info": true, // 表格左边显示搜索信息
        "autoWidth": true, // 自动计算表格宽度
        "stateSave": false, // 允许表格缓存Datatables，以便下次恢复之前的状态
        "retrieve": true, // 如果已经初始化了，则继续使用之前的Datatables实例
        "processing": true, // 显示正在处理的状态
        "serverSide": true, // 服务器模式，数据由服务器掌控
        "pagingType": "simple_numbers", // 翻页显示: 上一页和下一页两个按钮，加上页数按钮
        "language": {
            "sProcessing": "处理中...",
            "sLengthMenu": "显示 _MENU_ 项结果",
            "sZeroRecords": "没有匹配结果",
            "sInfo": "显示第 _START_ 至 _END_ 项结果，共 _TOTAL_ 项",
            "sInfoEmpty": "显示第 0 至 0 项结果，共 0 项",
            "sInfoFiltered": "(由 _MAX_ 项结果过滤)",
            "sInfoPostFix": "",
            "sUrl": "",
            "sEmptyTable": "未搜索到数据",
            "sLoadingRecords": "载入中...",
            "sInfoThousands": ",",
            "oPaginate": {
                "sFirst": "首页",
                "sPrevious": "上页",
                "sNext": "下页",
                "sLast": "末页"
            },
            "oAria": {
                "sSortAscending": ": 以升序排列此列",
                "sSortDescending": ": 以降序排列此列"
            }
        },
        columns: [// 绑定数据源
            {
                data: "name",
            }, {
                data: "author",
            }, {
                data: "price",
            }, {
                data: "publishingTime",
            }, {
                data: "categoryName",
            }, {
                data: null,
                orderable: false
            }
        ],
        columnDefs: [
            { // 定义表格样式
                targets: 3,
                render: function (data, type, row, meta) {
                    return (new Date(data)).Format("yyyy-MM-dd");
                }
            }, {
                targets: 5,
                render: function (data, type, row, meta) {
                    return '<td><a href="/book/update/'+row.id+'">更新</a>' +
                        '<a href="/book/delete/'+row.id+'">删除</a></td>';
                }
            }],
        ajax: {
            type: "POST",
            url: "/book/list", // 服务器url
            cache: false,
            data: function (data) {
                //参数  DataTableSearch
                var postData = {};

                var orderColumn = data.columns[data.order[0].column];
                postData.direction = data.order[0].dir;
                postData.orderBy = orderColumn.data;

                postData.draw = data.draw;
                postData.start = data.start;
                postData.length = data.length;

                var name = $("#name").val(),
                    author = $("#author").val(),
                    minPrice = $("#minPrice").val(),
                    maxPrice = $("#maxPrice").val(),
                    minTime = $("#minTime").val(),
                    maxTime = $("#maxTime").val(),
                    categoryId = $("#categoryId").val(),
                    keyWord = $("#keyWord").val();
                if (keyWord.length > 0) {
                    postData.keyWord = keyWord;
                }
                /*if (name.length > 0) {
                    postData.name = name;
                }
                if (author.length > 0) {
                    postData.author = author;
                }*/
                if (minPrice.length > 0) {
                    postData.minPrice = minPrice;
                }
                if (maxPrice.length > 0) {
                    postData.maxPrice = maxPrice;
                }
                if (minTime.length > 0) {
                    postData.minTime = minTime;
                }
                if (maxTime.length > 0) {
                    postData.maxTime = maxTime;
                }
                if (categoryId.length > 0) {
                    postData.categoryId = categoryId;
                }

                console.log(postData);
                return postData;
            }

        }
    });

    $("#clearBtn").on("click", function () {
        $("#bookForm")[0].reset();
    });
    $("#submitBtn").on("click", function () {
        reloadTable();
    });

    function reloadTable() {
        table.ajax.reload();
    }
});


