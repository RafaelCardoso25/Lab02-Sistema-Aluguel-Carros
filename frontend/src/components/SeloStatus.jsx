import { STATUS } from '../utils/formato.js'

export default function SeloStatus({ status }) {
  const { rotulo, classe } = STATUS[status]
  return <span className={`selo ${classe}`}>{rotulo}</span>
}
