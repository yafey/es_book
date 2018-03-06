$(function () {
    $("#clearBtn").on("click", function () {
        $("#bookForm")[0].reset();
    });
    $("#submitBtn").on("click", function () {
        $("#pageNumber").val(1);
        $("#bookForm").submit();
    });
});
function toPage(pageNumber) {
    $("#pageNumber").val(pageNumber);
    $("#bookForm").submit();
}