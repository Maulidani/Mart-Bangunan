<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Factories\HasFactory;

class User extends Model
{
    use HasFactory;

    protected $table = 'users';

    protected $fillable = [
        'name', 'phone', 'image','address_id','user_account_id','npwp',
    ];

    public function address()
    {
        return $this->belongsTo(Address::class);
    }

    public function account()
    {
        return $this->belongsTo(UserAccount::class);
    }
}