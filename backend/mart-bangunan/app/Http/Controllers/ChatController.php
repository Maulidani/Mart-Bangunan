<?php

namespace App\Http\Controllers;

use App\Models\Cart;
use App\Models\Chat;
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

class ChatController extends Controller
{
    public function addChat(Request $request)
    {
        $from = $request->user()->id;
        $to = $request->to_user_id;
        $message = $request->message;

        $chat = new Chat();
        $chat->from_user_id = $from;
        $chat->to_user_id = $to;
        $chat->message = $message;
        $chat->save();

        return response()->json([
            'message' => 'Success',
            'errors' => false,
        ]);
    }

    public function getChat(Request $request)
    {
        $chat = Chat::where([
            ['from_user_id', '=', $request->user()->id],
            ['to_user_id', '=',  $request->to_user_id],
        ])->orWhere([
            ['from_user_id', '=', $request->to_user_id],
            ['to_user_id', '=',  $request->user()->id],
        ])->orderBy('created_at', 'ASC')
            ->get();

        return response()->json([
            'message' => 'Success',
            'errors' => false,
            'chat' => $chat,
        ]);
    }

    public function listChat(Request $request)
    {
        if($request->user()->type==="seller") {
            $chat = Chat::join('users', 'users.user_account_id', '=', 'chats.from_user_id')
            ->orWhere('chats.to_user_id', $request->user()->id)
            ->orderBy('chats.created_at', 'DESC')
            ->get();
        $unique = $chat->unique('to_user_id')->values();
        } else {
            $chat = Chat::join('users', 'users.user_account_id', '=', 'chats.to_user_id')
            ->orWhere('chats.from_user_id', $request->user()->id)
            ->orderBy('chats.created_at', 'DESC')
            ->get();
        $unique = $chat->unique('to_user_id')->values();
        }


        return response()->json([
            'message' => 'Success',
            'errors' => false,
            'chat' => $unique,
        ]);
    }
}