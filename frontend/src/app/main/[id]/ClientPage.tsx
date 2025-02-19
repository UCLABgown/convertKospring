'use client'

import type { components } from '@/lib/backend/apiV1/schema'

type OrderResponseDTO = components['schemas']['OrderResponseDTO']

export default function ClientPage({ order }: { order: OrderResponseDTO }) {
  if (!order) {
    return <p>주문 데이터를 불러올 수 없습니다.</p>
  }

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

  return (
    <div className="max-w-[600px] mx-auto">
      <h2 className="text-5xl font-extrabold mt-20 mb-10 text-center">
        Order Details
      </h2>

      {/* 주문 상세 정보 섹션 */}
      <div className="border border-[#eee] rounded-[8px] bg-white shadow-[0_0_10px_0_rgba(0,0,0,0.1)] p-5 mb-16">
        <p className="text-sm text-gray-400">
          <CustomTime input={order.createdAt.toLocaleString()} />
        </p>

        <p className="text-lg">
          <strong>주문 번호</strong> {order.id}
        </p>
        <div className="flex justify-between items-center">
          <p className="my-3 text-3xl font-black">
            {order.totalPrice.toLocaleString()}원
          </p>
          <p>
            <span className="block px-[10px] py-[7px] rounded-[8px] border-2 border-[#59473F] text-[#59473F] font-bold">
              {{
                UNKNOWN: '알 수 없음',
                CANCELLED: '취소',
                PAYMENT_COMPLETED: '결제 완료',
                PREPARING: '배송 준비',
                SHIPPING: '배송 중',
                COMPLETED: '배송 완료',
              }[order.status] || '알 수 없음'}
            </span>
          </p>
        </div>
      </div>

      {/* 주문 상품 목록 제목 */}
      <h3 className="text-3xl mb-5 font-semibold">Order Items</h3>

      {/* 주문 상품 목록 섹션 */}
      {order.orderItems && order.orderItems.length > 0 ? (
        <ul style={{ listStyleType: 'none', padding: '0' }}>
          {order.orderItems.map((item, index) => (
            <li
              key={index}
              className="border border-[#eee] rounded-[8px] bg-white shadow-[0_0_10px_0_rgba(0,0,0,0.1)] p-5 mb-5"
            >
              <p className="text-2xl font-bold mb-2">{item.productName}</p>
              <p className="text-lg text-gray-600">
                {item.count}개{' '}
                <span className="inline-block w-[1px] h-[13px] bg-gray-300 mx-3"></span>{' '}
                {item.price.toLocaleString()}원
              </p>
            </li>
          ))}
        </ul>
      ) : (
        <p className="text-gray-500">주문 상품이 없습니다.</p>
      )}

      {/* 돌아가기 버튼 */}
      <div style={{ marginTop: '30px', textAlign: 'center' }}>
        <button
          onClick={() => window.history.back()}
          className="w-[150px] h-[50px] bg-[#59473F] text-white rounded-[8px] h-[40px]"
        >
          돌아가기
        </button>
      </div>
    </div>
  )
}
