'use client'

import type { components } from '@/lib/backend/apiV1/schema'
import { useState } from 'react'
import Link from 'next/link'
import client from '@/lib/backend/client'

type OrderItemResponseDTO = components['schemas']['OrderItemResponseDTO']
type OrderResponseDTO = components['schemas']['OrderResponseDTO']

import React from 'react'

export default function ClientPage() {
  const [email, setEmail] = useState<string>('')
  const [orders, setOrders] = useState<OrderResponseDTO[] | null>(null)
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState<boolean>(false)
  const [currentPage, setCurrentPage] = useState<number>(1)

  const itemsPerPage = 3

  const CustomTime = ({ input }: { input: string }) => {
    const parts = input.split(',')
    const year = parseInt(parts[0].trim() + parts[1].trim())
    const month = parseInt(parts[2].trim()).toString().padStart(2, '0')
    const day = parseInt(parts[3].trim()).toString().padStart(2, '0')
    var hour =
      parseInt(parts[4].trim()) >= 12
        ? parseInt(parts[4].trim()) - 12
        : parseInt(parts[4].trim())
    var after = parts[4] >= 12 ? '오후' : '오전'
    const minute = parseInt(parts[5].trim())
    return `${year}. ${month}. ${day}.   ${after} ${hour}시 ${minute}분`
  }

  const getOrdersByEmail = async () => {
    if (!email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
      setError('유효한 이메일을 입력해주세요.')
      setOrders(null)
      return
    }

    setLoading(true)
    setError(null)
    setOrders(null)
    setCurrentPage(1) // 새로운 조회 시 페이지를 초기화

    try {
      const data = await client.GET(
        `/order/by-email?email=${encodeURIComponent(email)}`,
      )
      setOrders(data.data.content) // 응답 데이터를 바로 저장
    } catch (err) {
      setError(err.message || '알 수 없는 오류가 발생했습니다.')
    } finally {
      setLoading(false)
    }
  }

  const handlePageChange = (page: number) => {
    setCurrentPage(page)
  }

  const paginatedOrders = orders
    ? orders.slice((currentPage - 1) * itemsPerPage, currentPage * itemsPerPage)
    : []

  const totalPages = orders ? Math.ceil(orders.length / itemsPerPage) : 0

  return (
    <div className="max-w-[1000px] mx-auto">
      <h2 className="text-5xl font-extrabold mt-20 mb-3 text-center">
        Order History
      </h2>
      <p className="mb-10 text-gray-400 text-center">
        주문 시 기입한 이메일을 통해 조회가 가능합니다.
      </p>
      <div className="flex gap-2 max-w-[650px] mx-auto">
        <input
          type="text"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          placeholder="예) example@naver.com"
          className="p-2 border border-[#ddd] h-[50px] rounded-[8px] grow"
        />
        <button
          onClick={getOrdersByEmail}
          className="w-[130px] h-[50px] rounded-[8px] bg-[#59473F] text-white"
          disabled={loading}
        >
          {loading ? '조회 중...' : '주문 조회'}
        </button>
      </div>

      {error && (
        <p className="max-w-[650px] mx-auto text-red-800 mt-2">{error}</p>
      )}
      {orders && (
        <div className="mt-10 pt-10 border-t border-[#ddd] border-dashed">
          <p className="mb-3 text-xl">
            <strong className="font-semibold">
              {orders.length}건의 주문 내역
            </strong>
            을 조회하였습니다.
          </p>
          {paginatedOrders.length > 0 ? (
            <table className="table-fixed w-full bg-white mt-3 shadow-[0_0_10px_0_rgba(0,0,0,0.1)]">
              <colgroup>
                <col className="w-[120px]" />
                <col className="w-[130px]" />
                <col className="" />
                <col className="w-[150px]" />
                <col className="w-[120px]" />
                <col className="w-[150px]" />
              </colgroup>
              <thead className="text-center bg-[#59473F] text-white">
                <tr>
                  <th className="py-3">주문 번호</th>
                  <th>주문 일자</th>
                  <th>주문 내역</th>
                  <th>주문 금액</th>
                  <th>배송 상태</th>
                  <th>&nbsp;</th>
                </tr>
              </thead>
              <tbody>
                {paginatedOrders.map((order, index) => (
                  <tr
                    key={index}
                    className="border-b border-[#eee] bg-white text-center"
                  >
                    <td className="p-3">{order.id}</td>
                    <td className="p-3">
                      <CustomTime input={order.createdAt.toLocaleString()} />
                    </td>
                    <td className="px-3">
                      {order.orderItems[0].productName} 외{' '}
                      {order.orderItems.length - 1}건
                    </td>
                    <td className="px-3">
                      {order.totalPrice.toLocaleString()}원
                    </td>
                    <td className="px-3">
                      {{
                        UNKNOWN: '알 수 없음',
                        CANCELLED: '취소',
                        PAYMENT_COMPLETED: '결제 완료',
                        PREPARING: '배송 준비',
                        SHIPPING: '배송 중',
                        COMPLETED: '배송 완료',
                      }[order.status] || '알 수 없음'}
                    </td>
                    <td className="px-3">
                      <Link
                        href={`/main/${order.id}`}
                        className="px-[10px] py-[7px] rounded-[8px] border border-[#59473F] text-[#59473F]"
                      >
                        더보기
                      </Link>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          ) : (
            <p className="text-gray-500">주문이 존재하지 않습니다.</p>
          )}

          {totalPages > 1 && (
            <div className="flex my-5 gap-2 justify-center">
              {Array.from({ length: totalPages }, (_, i) => i + 1).map(
                (page) => (
                  <button
                    key={page}
                    onClick={() => handlePageChange(page)}
                    className={`px-3 py-1 border rounded shadow-[0_0_10px_0_rgba(0,0,0,0.1)] ${
                      currentPage === page
                        ? 'bg-[#59473F] text-white'
                        : 'bg-white'
                    }`}
                  >
                    {page}
                  </button>
                ),
              )}
            </div>
          )}
        </div>
      )}
    </div>
  )
}
