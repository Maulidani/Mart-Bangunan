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
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Auth;

class ProductController extends Controller
{
    public function index(Request $request)
    {
        $product = collect(Product::join('image_products', 'products.id', '=', 'image_products.product_id')->orderBy('products.created_at', 'DESC')->get());

        $unique = $product->unique('product_id');
        $unique->values()->all();

        return response()->json([
            'message' => 'Success',
            'errors' => false,
            'product' => $unique,
        ]);
    }

    public function upload(Request $request)
    {

        $product = new Product;
        $product->user_id = $request->user()->id;
        $product->name = $request->name;
        $product->product_category_id = $request->category;
        $product->quantity = $request->quantity;
        $product->price = $request->price;
        // $product->discount = $request->discount;
        $product->save();

        $files = $request->image;
        $allowedfileExtension = ['jpeg', 'jpg', 'png', 'JPG', 'JPEG'];

        if ($request->hasfile('image')) { {
                foreach ($files as $img) {

                    $filename = time() . '.' . $img->getClientOriginalName();
                    $extension = $img->getClientOriginalExtension();

                    $check = in_array($extension, $allowedfileExtension);

                    if ($check) {

                        $img->move(public_path() . '/image/product/', $filename);

                        $product_image = new ProductImage;
                        $product_image->product_id = $product->id;
                        $product_image->image = $filename;
                        $product_image->save();
                    }
                }
            }
        }

        return response()->json([
            'message' => 'Success',
            'errors' => false,
        ]);
    }

    public function editProduct(Request $request)
    {

        $product = Product::find($request->id);
        $product->name = $request->name;
        $product->product_category_id = $request->category;
        $product->quantity = $request->quantity;
        $product->price = $request->price;
        // $product->discount = $request->discount;
        $product->save();

        // Product::where(
        //     'id',
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

    public function deleteImage(Request $request)
    {
        ProductImage::where(
            'id',
            $request->id
        )->delete();

        return response()->json([
            'message' => 'Success',
            'errors' => false,
        ]);
    }

    public function addImage(Request $request)
    {

        $files = $request->image;
        $allowedfileExtension = ['jpeg', 'jpg', 'png', 'JPG', 'JPEG'];

        if ($request->hasfile('image')) { {
                foreach ($files as $img) {

                    $filename = time() . '.' . $img->getClientOriginalName();
                    $extension = $img->getClientOriginalExtension();

                    $check = in_array($extension, $allowedfileExtension);

                    if ($check) {

                        $img->move(public_path() . '/image/product/', $filename);

                        $product_image = new ProductImage;
                        $product_image->product_id = $request->product_id;
                        $product_image->image = $filename;
                        $product_image->save();
                    }
                }
            }
        }

        return response()->json([
            'message' => 'Success',
            'errors' => false,
        ]);
    }
}