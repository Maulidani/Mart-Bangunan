<?php

namespace App\Models;

use Illuminate\Contracts\Auth\MustVerifyEmail;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Factories\HasFactory;

class ReviewOrder extends Model
{
    use HasFactory;

    protected $table = 'review_orders';

    protected $fillable = [
        'id','product_id', 'comment', 'rating', 'image',
    ];

}