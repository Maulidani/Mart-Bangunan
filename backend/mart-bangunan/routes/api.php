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
    Route::post('edit', 'UserAccountController@edit');
    Route::post('edit-image', 'UserAccountController@editImage');

    //product
    Route::post('upload-product', 'ProductController@upload');
    Route::post('delete-image-product', 'ProductController@deleteImage');
    Route::post('add-image-product', 'ProductController@addImage');
    Route::post('edit-product', 'ProductController@editProduct');
    Route::post('all-product', 'ProductController@index');
    Route::post('delete-product', 'ProductController@deleteProduct');
    Route::post('category-product', 'ProductController@categoryProduct');
    Route::post('seller-product', 'ProductController@sellerProduct');
    Route::post('image-product', 'ProductController@imageProduct');

    //order
    // Route::post('add-order', 'OrderController@addOrder');

    //cart
    Route::post('add-cart', 'CartController@AddCart');
    Route::post('my-cart', 'CartController@myCart');
    Route::post('delete-cart', 'CartController@deleteCart');

    //order
    Route::post('add-order', 'OrderController@addOrder');
    Route::post('get-order', 'OrderController@getOrder');
    Route::post('verification-order', 'OrderController@verificationOrder');
    Route::post('product-order', 'OrderController@getProductOrder');

    //chat
    Route::post('chat', 'ChatController@getChat');
    Route::post('add-chat', 'ChatController@addChat');
    Route::post('list-chat', 'ChatController@listChat');

});

Route::post('/midtrans/charge', 'OrderController@midtrans');