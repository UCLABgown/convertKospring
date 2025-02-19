'use client'

import type { components } from '@/lib/backend/apiV1/schema'
import React, { useState } from 'react'
import Cookies from 'js-cookie'
import { useRouter } from 'next/navigation'
import Link from 'next/link'

export default function ClientPage({
  searchKeyword,
  searchKeywordType,
  page,
  pageSize,
  responseBodyCategory,
  responseBody,
}: {
  searchKeyword: string
  searchKeywordType: string
  page: number
  pageSize: number
  responseBodyCategory: components['schemas']['ApiResponseListProductDto']
  responseBody: components['schemas']['ApiResponsePageDtoProductDto']
}) {
  const products = responseBody.content?.items ?? [] // 기본값 설정
  const [counts, setCounts] = useState<number[]>(products.map(() => 1)) // 초기 수량 설정

  const handleCountChange = (index: number, value: number) => {
    setCounts((prevCounts) => {
      const newCounts = [...prevCounts]
      newCounts[index] = Math.max(1, value) // 최소값 1
      return newCounts
    })
  }

  const handleAddToCart = (product, count) => {
    const cart = Cookies.get('cart')
    const cartItems = cart ? JSON.parse(cart) : []

    // 기존 상품 여부 확인 및 수량 업데이트
    const existingIndex = cartItems.findIndex(
      (item) => item.productName === product.productName,
    )

    if (existingIndex !== -1) {
      cartItems[existingIndex].count += count
    } else {
      cartItems.push({
        productName: product.productName,
        price: product.price,
        imageUrl: product.imageUrl,
        category: product.category,
        count,
      })
    }

    Cookies.set('cart', JSON.stringify(cartItems), { expires: 1 }) // 1일 동안 저장
    alert(`${product.productName}가 ${count}개 장바구니에 추가되었습니다.`)
  }
  const router = useRouter()

  const options = Array.from({ length: 99 }, (_, i) => i + 1)

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault()

    const formData = new FormData(e.target as HTMLFormElement)
    const searchKeyword = formData.get('searchKeyword') as string

    router.push(
      `?page=1&searchKeywordType=productName&searchKeyword=${searchKeyword}`,
    )
  }

  const handleOnchange = async (e: React.ChangeEvent<HTMLSelectElement>) => {
    const searchKeyword = e.target.value

    router.push(
      `?page=1&searchKeywordType=category&searchKeyword=${searchKeyword}`,
    )
  }

  return (
    <div className="flex flex-col">
      <h2 className="text-5xl font-extrabold mt-20 mb-10 text-center">
        Products
      </h2>

      <div className="flex justify-end">
        <div className="flex items-center">
          <label
            htmlFor="category"
            className="block mr-3 font-bold text-[#59473F]"
          >
            카테고리 검색
          </label>
          <select
            className="py-3 px-5 mr-5 block w-[200px] h-[50px] leading-[50px] border border-[#ddd] rounded-[8px]"
            id="category"
            onChange={handleOnchange}
          >
            <option value="전체">전체</option>
            {responseBodyCategory?.content?.map((item, index) => (
              <option key={item.category} value={item.category}>
                {item.category}
              </option>
            ))}
          </select>
          <form className="flex items-center" onSubmit={handleSubmit}>
            <label
              htmlFor="searchKeyword"
              className="block mr-3 font-bold text-[#59473F]"
            >
              키워드 검색
            </label>
            <input
              type="text"
              name="searchKeyword"
              id="searchKeyword"
              className="py-3 px-5 mr-3 block w-[250px] h-[50px] leading-[50px] border border-[#ddd] rounded-[8px]"
            />
            <button
              type="submit"
              className="h-[50px] leading-[50px] px-5 block bg-[#59473F] text-white rounded-[8px]"
            >
              검색
            </button>
          </form>
        </div>
      </div>

      {products.length > 0 ? (
        <ul className="grid grid-cols-4 gap-6">
          {products.map((product, index) => (
            <li
              key={index}
              className="border-[1px] my-3 flex flex-col bg-white shadow-[0_0_10px_0_rgba(0,0,0,0.1)]"
            >
              {/* 이미지 */}
              <div
                style={{
                  backgroundImage: `url(${product.imageUrl})`,
                  backgroundPosition: 'center',
                  backgroundSize: 'cover',
                  paddingBottom: '100%',
                  overflow: 'hidden',
                  textIndent: '-5000px',
                }}
                className="border-[#eee] border-b"
              ></div>
              {/* 상품 정보 */}
              <div className="p-3 flex-grow grid grid-rows-[auto_1fr_auto_auto]">
                {/* 카테고리 */}
                <div>
                  <span className="text-sm inline-block bg-[#59473F] font-bold text-white py-1 px-2 rounded-md">
                    {product.category}
                  </span>
                </div>
                {/* 이름 */}
                <div className="my-2 justify-self-start">
                  {product.productName}
                </div>
                {/* 가격 */}
                <div className="w-full mb-2 justify-self-end text-3xl font-extrabold text-[#59473F]">
                  {product.price.toLocaleString('ko-KR')}
                  <span className="text-2xl align-bottom">원</span>
                </div>
                <div className="mt-2 w-full justify-self-end relative">
                  {/* 수량 선택 */}
                  <select
                    id={`count-${index}`}
                    value={counts[index]}
                    onChange={(e) =>
                      handleCountChange(index, parseInt(e.target.value, 10))
                    }
                    className="w-[60px] border-[1px] border-[#ccc] rounded-[5px] p-2 h-[40px]"
                  >
                    {options.map((option) => (
                      <option key={option} value={option}>
                        {option}
                      </option>
                    ))}
                  </select>
                  {/* 추가 버튼 */}
                  <button
                    type="button"
                    className="absolute right-0 -bottom-0 py-2 px-3 bg-[#59473F] text-white rounded-[8px] h-[40px]"
                    onClick={() => handleAddToCart(product, counts[index])}
                  >
                    CART
                  </button>
                </div>
              </div>
            </li>
          ))}
        </ul>
      ) : (
        <p className="text-center text-2xl font-bold">상품이 없습니다.</p>
      )}

      <div className="flex my-5 gap-2 justify-center">
        {Array.from(
          { length: responseBody.content?.totalPages || 0 },
          (_, i) => i + 1,
        ).map((pageNum) => (
          <Link
            key={pageNum}
            className={`px-3 py-1 border rounded shadow-[0_0_10px_0_rgba(0,0,0,0.1)] ${
              pageNum === responseBody.content?.currentPageNumber
                ? 'bg-[#59473F] text-white'
                : 'bg-white'
            }`}
            href={`?page=${pageNum}`}
          >
            {pageNum}
          </Link>
        ))}
      </div>
    </div>
  )
}
