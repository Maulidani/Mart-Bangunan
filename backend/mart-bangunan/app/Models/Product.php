<?php

namespace App\Models;

use Illuminate\Contracts\Auth\MustVerifyEmail;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Factories\HasFactory;

class Product extends Model
{
    use HasFactory;

    protected $table = 'products';

    protected $fillable = [
        'id','user_id', 'name', 'product_category_id', 'quantity', 'price', 'discount'
    ];

    public function user()
    {
        return $this->belongsTo(UserAccount::class);
    }

    public function image()
    {
        return $this->belongsTo(ProductImage::class);
    }

    public function category()
    {
        return $this->belongsTo(ProductCategory::class);
    }
}