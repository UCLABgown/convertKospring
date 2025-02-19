'use client'

import type {components} from '@/lib/backend/apiV1/schema';
import React, {useState} from 'react';
import Cookies from 'js-cookie';

export default function ClientPage({
                                       responseBody,
                                   }: {
    responseBody: components['schemas']['ApiResponseListProductDto'];
}) {
    const products = responseBody.content.items ?? []; // 기본값 설정
    const [counts, setCounts] = useState<number[]>(products.map(() => 1)); // 초기 수량 설정

    const handleCountChange = (index: number, value: number) => {
        setCounts((prevCounts) => {
            const newCounts = [...prevCounts];
            newCounts[index] = Math.max(1, value); // 최소값 1
            return newCounts;
        });
    };

    const handleAddToCart = (product, count) => {
        const cart = Cookies.get('cart')
        const cartItems = cart ? JSON.parse(cart) : [];

        // 기존 상품 여부 확인 및 수량 업데이트
        const existingIndex = cartItems.findIndex(
            (item) => item.productName === product.productName
        );

        if (existingIndex !== -1) {
            cartItems[existingIndex].count += count;
        } else {
            cartItems.push({
                productName: product.productName,
                price: product.price,
                imageUrl: product.imageUrl,
                category: product.category,
                count,
            });
        }

        Cookies.set('cart', JSON.stringify(cartItems), {expires: 1}); // 1일 동안 저장
        alert(`${product.productName}가 ${count}개 장바구니에 추가되었습니다.`);
    };

    const options = Array.from({ length: 99 }, (_, i) => i + 1);

    return (
        <div>
            <h1 className="text-2xl my-5 font-bold text-gray-800"> 상품 목록 </h1>

            {products.length > 0 ? (
                <ul className="list-none p-0">
                    {products.map((product, index) => (
                        <li
                            key={index}
                            className="flex  p-4 border border-gray-300 rounded-lg bg-gray-100 shadow-md mb-4"
                        >

                            {/* 이미지 */}
                            <img
                                src={product.imageUrl}
                                alt={product.productName}
                                className="w-20 h-20 object-cover mr-3 rounded-md"
                            />

                            {/* 상품 정보 */}
                            <div style={{flex: 1}}>

                                {/* 이름 */}
                                <h2 className="text-base font-bold m-0">{product.productName}</h2>

                                {/* 가격 */}
                                <p className="text-sm my-1">
                                    {product.price.toLocaleString('ko-KR')}원
                                </p>

                                {/* 카테고리 */}
                                <p className="inline-block text-xs font-bold text-white bg-coffee py-1 px-2 rounded-md mt-1">
                                    {product.category}
                                </p>
                            </div>

                            <div className="flex items-center gap-4">
                                {/* 수량 선택 */}
                                <div className="flex items-center gap-2">
                                    <label htmlFor={`count-${index}`} className="text-sm">
                                        수량:
                                    </label>
                                    <select
                                        id={`count-${index}`}
                                        value={counts[index]}
                                        onChange={(e) => handleCountChange(index, parseInt(e.target.value, 10))}
                                        className="p-1 border border-gray-300 rounded-md"
                                    >
                                        {options.map((option) => (
                                            <option key={option} value={option}>
                                                {option}
                                            </option>
                                        ))}
                                    </select>
                                </div>

                                {/* 추가 버튼 */}
                                <button
                                    className="h-10 py-2 px-3 bg-white border border-gray-300 rounded-md hover:bg-gray-200 cursor-pointer"
                                    onClick={() => handleAddToCart(product, counts[index])}
                                >
                                    추가
                                </button>
                            </div>
                        </li>
                    ))}
                </ul>
            ) : (
                <p>상품이 없습니다.</p>
            )}
        </div>
    );
}