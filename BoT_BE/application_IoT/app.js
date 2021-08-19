var createError = require('http-errors');
var express = require('express');
var path = require('path');

var apiRouter = require('./routes/api');
var app = express();

const PORT = 3001;
const HOST = '0.0.0.0';

app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(express.static(path.join(__dirname, 'public')));

app.use('/iot', apiRouter);

// catch 404 and forward to error handler
app.use(function(req, res, next) {
  next(createError(404));
});


// error handler
app.use(function(err, req, res, next) {
  // set locals, only providing error in development
  res.locals.message = err.message;
  res.locals.error = req.app.get('env') === 'development' ? err : {};

  // render the error page
  res.status(err.status || 500);
  res.send('error');
});


// 서버시작
app.listen(PORT, HOST);
console.log(`Running on http://${HOST}:${PORT}`);