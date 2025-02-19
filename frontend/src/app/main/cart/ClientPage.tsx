'use client'

import type { components } from '@/lib/backend/apiV1/schema'
import React from 'react'
import Cookies from 'js-cookie'
import client from '@/lib/backend/client'
import { useRouter } from 'next/navigation'

type CartItem = {
  productName: string
  price: number
  imageUrl: string
  category: string
  count: number
}

export default function ClientPage({
  responseBody,
}: {
  responseBody: components['schemas']['ApiResponseListProductDto']
}) {
  const router = useRouter()
  const [cartItems, setCartItems] = React.useState<CartItem[]>([])

  React.useEffect(() => {
    const cart = Cookies.get('cart')
    setCartItems(cart ? JSON.parse(cart) : [])
  }, [])

  const totalPrice = cartItems.reduce(
    (total: number, item: { count: number; price: number }) =>
      total + item.count * item.price,
    0,
  )

  const validateEmail = (email: string) => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    if (!email) {
      return false
    }
    if (!emailRegex.test(email)) {
      return false
    }
    return true
  }

  const handleDelete = (productName: string) => {
    const updatedCart = cartItems.filter(
      (item) => item.productName !== productName,
    ) // 이름 기준으로 필터링
    setCartItems(updatedCart) // 상태 업데이트
    Cookies.set('cart', JSON.stringify(updatedCart)) // 쿠키에 저장
  }

  const handlePayment = async () => {
    const emailInput = document.getElementById('email') as HTMLInputElement
    const email = emailInput?.value || ''

    if (!validateEmail(email)) {
      alert('유효한 이메일 주소를 입력해주세요.')
      emailInput.focus()
      return
    }

    const requestBody = {
      body: {
        email,
        totalPrice,
        orderItems: cartItems.map(({ productName, count, price }) => ({
          productName,
          count,
          price,
        })),
      },
    }

    try {
      const response = await client.POST('/order', requestBody)

      if (response.data?.success) {
        alert('결제에 성공했습니다.')
        Cookies.remove('cart')
        router.push('/')
      } else {
        alert(response.error?.message || '결제에 실패했습니다.')
      }
    } catch (error) {
      alert('결제 요청 중 문제가 발생했습니다.')
    }
  }

  return (
    <div>
      <h2 className="text-5xl font-extrabold mt-20 mb-10 text-center">
        Cart & Order
      </h2>
      <style jsx>{`
        li:first-child {
          border: none;
        }
      `}</style>
      {cartItems.length > 0 ? (
        <div className="flex">
          {/* 왼쪽: 상품 목록 */}
          <div className="w-1/2 border-2 border-[#eee] rounded-[8px] bg-white shadow-[0_0_10px_0_rgba(0,0,0,0.1)] p-5 mr-4">
            <ul className="list-none p-0">
              {cartItems.map((item, index) => (
                <li
                  key={index}
                  className="flex gap-2 py-4 border-t border-[#ddd] border-dashed"
                >
                  {/* 이미지 */}
                  <div className="w-[80px] h-[80px] object-cover text-left rounded-md overflow-hidden shrink-0">
                    <img
                      src={item.imageUrl}
                      alt={item.productName}
                      className="w-full h-full object-cover"
                    />
                  </div>

                  {/* 상품 정보  */}
                  <div className="grow min-w-0">
                    {/* 이름 */}
                    <h3 className="text-sm font-semibold whitespace-nowrap overflow-hidden text-ellipsis">
                      {item.productName}
                    </h3>

                    {/* 가격 */}
                    <p className="my-1 font-black">
                      {' '}
                      {item.price.toLocaleString('ko-KR')}원{' '}
                    </p>

                    {/* 카테고리 */}
                    <p className="inline-block text-xs font-bold text-white bg-[#59473F] py-1 px-2 rounded-[5px] mt-1">
                      {item.category}
                    </p>
                  </div>

                  {/* 개수 */}
                  <div className="flex items-center justify-end w-[50px] shrink-0">
                    <span className="text-base font-medium">
                      {item.count} 개
                    </span>
                  </div>

                  {/* 결과 */}
                  <div className="flex items-center justify-end w-[170px] shrink-0">
                    {/* 최종 가격 */}
                    <span className="text-base font-bold">
                      {(item.count * item.price).toLocaleString('ko-KR')} 원
                    </span>

                    {/* 삭제 버튼 */}
                    <button
                      className="ml-3 px-3 py-2 bg-red-800 text-white text-sm font-medium rounded font-bold"
                      onClick={() => handleDelete(item.productName)}
                    >
                      삭제
                    </button>
                  </div>
                </li>
              ))}
            </ul>
          </div>

          {/* 오른쪽: 결제 페이지 */}
          <div className="flex flex-col w-1/2 p-5 pt-10 border-2 border-[#eee] rounded-[8px] bg-white shadow-[0_0_10px_0_rgba(0,0,0,0.1)]">
            {/* 가격 */}
            <div className="mb-10">
              <div className="flex justify-between items-center text-3xl font-black">
                <span>최종 결제 금액</span>
                <span className="ml-auto text-5xl">
                  {totalPrice.toLocaleString('ko-KR')} 원
                </span>
              </div>
            </div>

            {/* 이메일 */}
            <div className="mb-4 grow">
              <label htmlFor="email" className="block font-semibold mb-2">
                e-mail
              </label>
              <input
                id="email"
                type="email"
                className="w-full p-2 border border-gray-300 rounded-md text-gray-900"
                placeholder="example@email.com"
              />
            </div>
            {/* 배송 설명 */}
            <p className="text-m text-black my-3">
              <strong className="text-red-800">
                당일 오후 2시 이후의 주문
              </strong>
              은 다음날 배송을 시작합니다.
            </p>
            {/* 결제 버튼 */}
            <button
              className="w-full bg-[#59473F] text-white py-3 text-lg mt-3 rounded-md"
              onClick={handlePayment}
            >
              결제하기
            </button>
          </div>
        </div>
      ) : (
        <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2">
          <p className="text-2xl font-bold text-gray-700 text-center">
            장바구니에 담긴 상품이 없습니다.
            <span className="text-lg font-normal mt-1 text-gray-400 block">
              원하는 상품을 장바구니에 담아보세요!
            </span>
          </p>
          <button
            type="button"
            onClick={() => router.push('/main')}
            className="bg-[#59473F] text-white rounded-[8px] mt-5 mx-auto block w-[200px] py-3 text-lg"
          >
            쇼핑 계속 하기
          </button>
        </div>
      )}
    </div>
  )
}
