<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;

/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
|
| Here is where you can register API routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| is assigned the "api" middleware group. Enjoy building your API!
|
*/

Route::post('login', 'UserAccountController@login');
Route::post('register', 'UserAccountController@register');

Route::middleware(['auth:api'])->group(function () {
    Route::get('user', 'UserAccountController@index');
    Route::post('logout', 'UserAccountController@logout');

    //product
    Route::post('upload-product', 'ProductController@upload');
    Route::post('delete-image-product', 'ProductController@deleteImage');
    Route::post('add-image-product', 'ProductController@addImage');
    Route::post('edit-product', 'ProductController@editProduct');
    Route::get('all-product', 'ProductController@index');

});