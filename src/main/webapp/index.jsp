<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Form Submit Example</title>
</head>
<body>
<form id="myForm" action="/hello" method="POST">
    <!-- Добавьте необходимые поля формы -->
    <input type="text" name="username" placeholder="Enter your name">
    <button type="button" onClick="submitForm()">Submit</button>
</form>

<script>
    function submitForm() {
        document.getElementById("myForm").submit();
    }
</script>
</body>
</html>