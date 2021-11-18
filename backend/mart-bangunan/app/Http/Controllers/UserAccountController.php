<?php

namespace App\Http\Controllers;

use App\Models\User;
use Illuminate\Http\Request;
use App\Models\UserAccount;
use App\Models\UserAddress;
use Illuminate\Support\Facades\Auth;

class UserAccountController extends Controller
{

    public function index(Request $request)
    {
        $id = $request->user()->id;
        $user = User::join('addresses', 'users.address_id', '=', 'addresses.id')
            ->join('user_accounts', 'users.user_account_id', '=', 'user_accounts.id')
            ->where('user_accounts.id', $id)
            ->get(['users.*', 'addresses.name as address', 'addresses.*', 'user_accounts.*']);

        return response()->json([
            'message' => 'Success',
            'errors' => true,
            'user' => $user
        ]);;
    }

    public function register(Request $request)
    {

        $request->validate([
            'email' => 'required|email|unique:user_accounts',
            'password' => 'required',
        ]);

        $user_account = new UserAccount;
        $user_account->email = $request->email;
        $user_account->password = bcrypt($request->password);
        $user_account->save();

        $user_address = new UserAddress();
        $user_address->name = $request->address_name;
        $user_address->country = $request->country;
        $user_address->province = $request->province;
        $user_address->city = $request->city;
        $user_address->districts = $request->districts;
        $user_address->zip_code = $request->zip_code;
        $user_address->save();

        $files = $request->image;
        $allowedfileExtension = ['jpeg', 'jpg', 'png', 'JPG', 'JPEG'];
        if ($request->hasfile('image')) {

            $filename = time() . '.' . $files->getClientOriginalName();
            $extension = $files->getClientOriginalExtension();

            $check = in_array($extension, $allowedfileExtension);

            if ($check) {

                $files->move(public_path() . '/image/product/', $filename);

                $user = new User;
                $user->name = $request->name;
                $user->phone = $request->phone;
                $user->image = $filename;
                $user->address_id = $user_address->id;
                $user->user_account_id = $user_account->id;
                $user->save();
            }
        }


        if ($user && $user_account && $user_address) {

            return response()->json([
                'message' => 'Success',
                'errors' => false,
            ]);
        } else {

            return response()->json([
                'message' => 'Fail',
                'errors' => true,
            ]);
        }
    }

    public function login(Request $request)
    {

        $credentials = $request->validate([
            'email' => 'required|email',
            'password' => 'required',
        ]);

        if (Auth::attempt($credentials)) {
            $user = Auth::user();
            $token = md5(time()) . '.' . md5($request->email);
            $user->forceFill([
                'api_token' => $token,
            ])->save();

            return response()->json([
                'api_token' => $token
            ]);
        } else {
            return response()->json([
                'message' => 'the provided credentials do not match our records'
            ]);
        }
    }

    public function logout(Request $request)
    {

        $request->user()->forceFill([
            'api_token' => null
        ])->save();

        return response()->json(['message' => 'Success']);
    }
}