<?php

namespace App\Http\Controllers;

use App\Models\Product;
use App\Models\ProductImage;
use App\Models\User;
use App\Models\File;
use App\Models\ProductCategory;
use Symfony\Component\Console\Input\Input;
use Illuminate\Http\Request;
use App\Models\UserAccount;
use App\Models\UserAddress;
use App\Http\Controllers\chargeAPI;
use App\Models\Order;
use App\Models\ProductOrder;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Auth;

class OrderController extends Controller
{
    public function addOrder(Request $request)
    {

        $order = new Order();
        $order->user_id = $request->user()->id;
        $order->quantity = 0;
        $order->shipping = $request->shipping;
        $order->tax = 0;
        $order->total = $request->total;
        $order->note = $request->note;
        $order->order_id_midtrans = $request->order_id_midtrans;
        $order->status = 0;
        $order->save();

        foreach ($request->product_id as $product) {
            $product_order = new ProductOrder();
            $product_order->order_id = $order->id;
            $product_order->product_id = $product;
            $product_order->save();
        }

        return response()->json([
            'message' => 'Success',
            'errors' => false,
        ]);
    }

    public function getOrder(Request $request)
    {
        if ($request->user()->type === "seller") {

            $product = Order::join('product_orders', 'orders.id', '=', 'product_orders.order_id')
                ->orderBy('orders.created_at', 'DESC')
                ->get();
        } else {
            $product = Order::join('product_orders', 'orders.id', '=', 'product_orders.order_id')
                ->where('orders.user_id', $request->user()->id)
                ->orderBy('orders.created_at', 'DESC')
                ->get();
        }

        $unique = $product->unique('order_id')->values();

        return response()->json([
            'message' => 'Success',
            'errors' => false,
            'order' => $unique,
        ]);
    }

    public function getProductOrder(Request $request)
    {
        $product = ProductOrder::join('products', 'product_orders.product_id', '=', 'products.id')
        ->where('product_orders.order_id', $request->order_id)
        ->orderBy('product_orders.created_at', 'DESC')
        ->get();

        return response()->json([
            'message' => 'Success',
            'errors' => false,
            'product' => $product,
        ]);
    }

    public function verificationOrder(Request $request)
    {
        $order = Order::find($request->id);
        $order->status = $request->status;
        $order->save();

        //     $request->id
        // )->first()->forceFill([
        //     'name' => $request->name,
        //     'product_category_id' => $request->category,
        //     'quantity' => $request->quantity,
        //     'price' => $request->price,
        //     // 'discount' => $request->discount,
        // ])->save();

        return response()->json([
            'message' => 'Success',
            'errors' => false,
        ]);
    }


    public function midtrans(Request $request)
    {

        // Set your server key (Note: Server key for sandbox and production mode are different)
        $server_key = 'SB-Mid-server-mYq5M0siDK_EgTwmFhl5A9gb';
        // Set true for production, set false for sandbox
        $is_production = false;

        $api_url = $is_production ?
            'https://app.midtrans.com/snap/v1/transactions' :
            'https://app.sandbox.midtrans.com/snap/v1/transactions';


        // Check if request doesn't contains `/charge` in the url/path, display 404
        if (!strpos($_SERVER['REQUEST_URI'], '/charge')) {
            http_response_code(404);
            echo "wrong path, make sure it's `/charge`";
            exit();
        }
        // Check if method is not HTTP POST, display 404
        if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
            http_response_code(404);
            echo "Page not found or wrong HTTP request method is used";
            exit();
        }

        // get the HTTP POST body of the request
        $request_body = $request->getContent();
        // set response's content type as JSON
        // header('Content-Type: application/json');
        // call charge API using request body passed by mobile SDK
        // $charge_result = chargeAPI($api_url, $server_key, $request_body);

        $ch = curl_init();
        $curl_options = array(
            CURLOPT_URL => $api_url,
            CURLOPT_RETURNTRANSFER => 1,
            CURLOPT_POST => 1,
            CURLOPT_HEADER => 0,
            // Add header to the request, including Authorization generated from server key
            CURLOPT_HTTPHEADER => array(
                'Content-Type: application/json',
                'Accept: application/json',
                'Authorization: Basic ' . base64_encode($server_key . ':')
            ),
            CURLOPT_POSTFIELDS => $request_body
        );
        curl_setopt_array($ch, $curl_options);
        $result = array(
            'body' => curl_exec($ch),
            'http_code' => curl_getinfo($ch, CURLINFO_HTTP_CODE),
        );

        $charge_result = $result;
        // set the response http status code
        http_response_code($charge_result['http_code']);
        // then print out the response body
        echo $charge_result['body'];
    }
}
