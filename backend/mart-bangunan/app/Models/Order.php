<?php

namespace App\Models;

use Illuminate\Contracts\Auth\MustVerifyEmail;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Factories\HasFactory;

class Order extends Model
{
    use HasFactory;

    protected $table = 'orders';

    protected $fillable = [
        'id','user_id', 'quantity', 'shipping', 'tax', 'total', 'note','status_id'
    ];

    public function user()
    {
        return $this->belongsTo(UserAccount::class);
    }
}
