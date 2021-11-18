<?php

namespace App\Models;

use Illuminate\Contracts\Auth\MustVerifyEmail;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Foundation\Auth\User as Authenticatable;
use Illuminate\Notifications\Notifiable;
use Laravel\Sanctum\HasApiTokens;

class UserAddress extends Model
{
    use HasFactory;

    protected $table = 'addresses';

    protected $fillable = [
        'name', 'country', 'province','city','districts','zip_code'
    ];

}