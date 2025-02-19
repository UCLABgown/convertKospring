import client from '@/lib/backend/client';
import ClientPage from './ClientPage';

export default async function Page() {
  try {
    const apiResponse = await client.GET(`/order`);

    if (apiResponse.response.error) {
      return <p>{apiResponse.response.error}</p>;
    }

    return <ClientPage orders={apiResponse.data.content} />;
  } catch (error: any) {
    return <p>{error.message || '알 수 없는 오류가 발생했습니다.'}</p>;
  }
}