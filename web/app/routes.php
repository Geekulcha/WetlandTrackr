<?php

/*
|--------------------------------------------------------------------------
| Application Routes
|--------------------------------------------------------------------------
|
| Here is where you can register all of the routes for an application.
| It's a breeze. Simply tell Laravel the URIs it should respond to
| and give it the Closure to execute when that URI is requested.
|
*/

Route::group(array('prefix' => 'api/v1'), function() {
    Route::resource('users', 'UsersApiController');
    Route::resource('wetland_datas', 'WetlandDatasApiController');
});

Route::get('/', function()
{
	return View::make('hello');
});