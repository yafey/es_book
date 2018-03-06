var keyWord;
var minPrice;
var maxPrice;
var minTime;
var maxTime;
var categoryId;
$(function () {
    keyWord = $("#keyWord").val();
    minPrice = $("#minPrice").val();
    maxPrice = $("#maxPrice").val();
    minTime = $("#minTime").val();
    maxTime = $("#maxTime").val();
    categoryId = $("#categoryId").val();
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
    $("#keyWord").val(keyWord);
    $("#minPrice").val(minPrice);
    $("#maxPrice").val(maxPrice);
    $("#minTime").val(minTime);
    $("#maxTime").val(maxTime);
    $("#categoryId").val(categoryId);
    $("#bookForm").submit();
}