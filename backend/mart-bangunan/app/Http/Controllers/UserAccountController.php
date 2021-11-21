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
            ->get(['users.name as user_name','users.*', 'addresses.name as address', 'addresses.*', 'user_accounts.*'])->first();

        return response()->json([
            'message' => 'Success',
            'errors' => false,
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
        $user_account->type = $request->type;
        $user_account->save();

        $user_address = new UserAddress();
        $user_address->name = $request->address_name;
        $user_address->country = "indonesia";
        $user_address->province = $request->province;
        $user_address->city = $request->city;
        $user_address->districts = $request->districts;
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
                $user->npwp = $request->npwp;
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

            if ($user->type === $request->type) {
                $token = md5(time()) . '.' . md5($request->email);
                $user->forceFill([
                    'api_token' => $token,
                ])->save();

                return response()->json([
                    'message' => 'Success',
                    'errors' => false,
                    'api_token' => $token,
                ]);
                
            } else {
                return response()->json([
                    'message' => 'the provided credentials do not match our records',
                    'errors' => true,
                ]);
            }
            
        } else {
            return response()->json([
                'message' => 'the provided credentials do not match our records',
                'errors' => true,
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