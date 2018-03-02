$(function () {
    $("#clearBtn").on("click", function () {
        $("#bookForm")[0].reset();
    });
    $("#submitBtn").on("click", function () {
        $("#bookForm").submit();
    });
});