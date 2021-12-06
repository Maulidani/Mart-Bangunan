<?php

namespace App\Http\Controllers;

use App\Models\Cart;
use App\Models\Product;
use App\Models\ProductImage;
use App\Models\User;
use App\Models\File;
use App\Models\ProductCategory;
use Symfony\Component\Console\Input\Input;
use Illuminate\Http\Request;
use App\Models\UserAccount;
use App\Models\UserAddress;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Auth;

class CartController extends Controller
{
    public function addCart(Request $request)
    {
        $userId = $request->user()->id;
        $productId = $request->product_id;

        $exist = Cart::where([
            ['user_id', '=', $userId],
            ['product_id', '=', $productId]
        ])->exists();

        if ($exist) {

            return response()->json([
                'message' => 'Fail',
                'errors' => true,
            ]);
        } else {

            $cart = new Cart();
            $cart->user_id = $userId;
            $cart->product_id = $productId;
            // $cart->quantity = $request->quantity;
            $cart->save();

            return response()->json([
                'message' => 'Success',
                'errors' => false,
            ]);
        }
    }

    public function myCart(Request $request)
    {
        $product = Product::join('carts', 'products.id', '=', 'carts.product_id')
            ->join('image_products', 'products.id', '=', 'image_products.product_id')
            ->where('carts.user_id', $request->user()->id)
            ->orderBy('carts.created_at', 'DESC')
            ->get(['products.*', 'carts.id as cart_id', 'carts.*', 'image_products.*', ]);

        $unique = $product->unique('product_id')->values();

        return response()->json([
            'message' => 'Success',
            'errors' => false,
            'cart' => $unique,
        ]);
    }

    public function deleteCart(Request $request)
    {
        Cart::where(
            'id',
            $request->id
        )->delete();

        return response()->json([
            'message' => 'Success',
            'errors' => false,
        ]);
    }
}
