<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Factories\HasFactory;

class ProductImage extends Model
{
    use HasFactory;

    protected $table = 'image_products';

    protected $fillable = [
        'product_id','image',
    ];

    public function product()
    {
        return $this->belongsTo(Product::class);
    }

}