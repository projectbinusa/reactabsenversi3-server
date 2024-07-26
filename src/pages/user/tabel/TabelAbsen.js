import React, { useEffect, useState } from "react";
import Navbar from "../../../components/NavbarUser";
import Sidebar from "../../../components/SidebarUser";
import { Link } from "react-router-dom/cjs/react-router-dom.min";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faInfo,
  faSearch,
  faUserPlus,
} from "@fortawesome/free-solid-svg-icons";
import axios from "axios";
import { Pagination } from "flowbite-react";
import { API_DUMMY } from "../../../utils/api";

function TabelAbsen() {
  const [absensi, setAbsensi] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [limit, setLimit] = useState(5);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);

  const formatDate = (dateString) => {
    const options = {
      weekday: "long",
      year: "numeric",
      month: "long",
      day: "numeric",
    };
    return new Date(dateString).toLocaleDateString("id-ID", options);
  };

  const getAbsensi = async () => {
    const token = localStorage.getItem("token");
    const userId = localStorage.getItem("userId");

    try {
      const response = await axios.get(
        `${API_DUMMY}/api/absensi/getByUserId/${userId}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      setAbsensi(response.data);
    } catch (error) {
      console.error("Error fetching data:", error);
    }
  };

  useEffect(() => {
    getAbsensi();
  }, []);

  useEffect(() => {
    const filteredData = absensi.filter(
      (absenData) =>
        (absenData.jamMasuk?.toLowerCase().includes(searchTerm.toLowerCase()) ??
          false) ||
        (absenData.jamPulang
          ?.toLowerCase()
          .includes(searchTerm.toLowerCase()) ??
          false) ||
        (absenData.statusAbsen
          ?.toLowerCase()
          .includes(searchTerm.toLowerCase()) ??
          false) ||
        (absenData.keterangan
          ?.toLowerCase()
          .includes(searchTerm.toLowerCase()) ??
          false) ||
        (formatDate(absenData.tanggalAbsen)
          ?.toLowerCase()
          .includes(searchTerm.toLowerCase()) ??
          false)
    );
    setTotalPages(Math.ceil(filteredData.length / limit));
  }, [searchTerm, limit, absensi]);

  const handleSearch = (event) => {
    setSearchTerm(event.target.value);
  };

  const handleLimitChange = (event) => {
    setLimit(parseInt(event.target.value));
    setCurrentPage(1); // Reset to the first page when limit changes
  };

  function onPageChange(page) {
    setCurrentPage(page);
  }

  const filteredAbsen = absensi.filter(
    (absenData) =>
      (absenData.jamMasuk?.toLowerCase().includes(searchTerm.toLowerCase()) ??
        false) ||
      (absenData.jamPulang?.toLowerCase().includes(searchTerm.toLowerCase()) ??
        false) ||
      (absenData.statusAbsen
        ?.toLowerCase()
        .includes(searchTerm.toLowerCase()) ??
        false) ||
      (absenData.keteranganIzin
        ?.toLowerCase()
        .includes(searchTerm.toLowerCase()) ??
        false) ||
      (formatDate(absenData.tanggalAbsen)
        ?.toLowerCase()
        .includes(searchTerm.toLowerCase()) ??
        false)
  );

  const paginatedAbsen = filteredAbsen.slice(
    (currentPage - 1) * limit,
    currentPage * limit
  );

  return (
    <div className="flex flex-col min-h-screen">
      <div className="sticky top-0 z-50">
        <Navbar />
      </div>
      <div className="flex flex-1">
        <div className="fixed">
          <Sidebar />
        </div>
        <div className="content-page flex-1 p-8 md:ml-64 mt-16">
          <div className="tabel-absen bg-blue-100 p-5 rounded-xl shadow-xl border border-gray-300 text-center">
            <div className="flex justify-between">
              <h2 className="text-xl font-bold">History Absensi</h2>
              <div className="flex items-center gap-2 mt-2">
                <div className="relative w-64">
                  <input
                    type="search"
                    id="search-dropdown"
                    value={searchTerm}
                    onChange={handleSearch}
                    className="block p-2.5 w-full z-20 text-sm rounded-l-md text-gray-900 bg-gray-50 border-gray-300 focus:ring-blue-500 focus:border-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:border-blue-500"
                    placeholder="Search name..."
                    required
                  />
                </div>
                <select
                  value={limit}
                  onChange={handleLimitChange}
                  className="flex-shrink-0 z-10 inline-flex rounded-r-md items-center py-2.5 px-4 text-sm font-medium text-gray-900 bg-gray-100 border border-gray-300 hover:bg-gray-200 focus:ring-4 focus:outline-none focus:ring-gray-100 dark:bg-gray-700 dark:hover:bg-gray-600 dark:focus:ring-gray-700 dark:text-white dark:border-gray-600"
                >
                  <option value="5">05</option>
                  <option value="10">10</option>
                  <option value="20">20</option>
                  <option value="50">50</option>
                </select>
              </div>
            </div>
            <div className="overflow-x-auto rounded-xl border border-gray-200 mt-4">
              <table className="min-w-full divide-y-2 divide-gray-200 bg-white text-sm border border-gray-300">
                <thead className="text-left text-white bg-blue-500">
                  <tr>
                    <th className="whitespace-nowrap px-4 py-2 font-medium text-center">
                      NO
                    </th>
                    <th className="whitespace-nowrap px-4 py-2 font-medium text-center">
                      TANGGAL
                    </th>
                    <th className="whitespace-nowrap px-4 py-2 font-medium text-center">
                      JAM MASUK
                    </th>
                    <th className="whitespace-nowrap px-4 py-2 font-medium text-center">
                      JAM PULANG
                    </th>
                    <th className="whitespace-nowrap px-4 py-2 font-medium text-center">
                      KETERANGAN
                    </th>
                    <th className="whitespace-nowrap px-4 py-2 font-medium text-center">
                      KEHADIRAN
                    </th>
                    <th className="whitespace-nowrap px-4 py-2 font-medium text-center">
                      AKSI
                    </th>
                  </tr>
                </thead>

                <tbody className="divide-y divide-gray-200">
                  {paginatedAbsen.map((absenData, index) => (
                    <tr key={index}>
                      <td className="whitespace-nowrap px-4 py-2 font-medium text-gray-900 text-center">
                        {(currentPage - 1) * limit + index + 1}
                      </td>
                      <td className="whitespace-nowrap px-4 py-2 text-gray-700 text-center capitalize">
                        {formatDate(absenData.tanggalAbsen)}
                      </td>
                      <td className="whitespace-nowrap px-4 py-2 text-gray-700 text-center capitalize">
                        {absenData.jamMasuk}
                      </td>
                      <td className="whitespace-nowrap px-4 py-2 text-gray-700 text-center capitalize">
                        {absenData.jamPulang}
                      </td>
                      <td className="whitespace-nowrap px-4 py-2 text-gray-700 text-center capitalize">
                        {absenData.keteranganIzin != null
                          ? absenData.keteranganIzin
                          : absenData.keteranganTerlambat == null
                          ? "-"
                          : absenData.keteranganTerlambat}
                      </td>
                      <td className="whitespace-nowrap px-4 py-2 text-gray-700 text-center capitalize">
                        {absenData.statusAbsen}
                      </td>
                      <td className="whitespace-nowrap text-center py-3">
                        <div className="flex items-center -space-x-4 ml-12">
                          <Link to={"/user/detail_absen/" + absenData.id}>
                            <button className="z-20 block rounded-full border-2 border-white bg-blue-100 p-4 text-blue-700 active:bg-blue-50">
                              <span className="relative inline-block">
                                <FontAwesomeIcon
                                  icon={faInfo}
                                  className="h-4 w-4"
                                />
                              </span>
                            </button>
                          </Link>
                          {absensi.statusAbsen === "Izin" ? (
                            <button
                              className="z-30 block rounded-full border-2 border-white bg-gray-100 p-4 text-gray-700 active:bg-red-50"
                              disabled
                            >
                              <span className="relative inline-block">
                                <FontAwesomeIcon
                                  className="h-4 w-4"
                                  icon={faUserPlus}
                                />
                              </span>
                            </button>
                          ) : (
                            <Link to="/user/izin_absen">
                              <button className="z-30 block rounded-full border-2 border-white bg-red-100 p-4 text-red-700 active:bg-red-50">
                                <span className="relative inline-block">
                                  <FontAwesomeIcon
                                    className="h-4 w-4"
                                    icon={faUserPlus}
                                  />
                                </span>
                              </button>
                            </Link>
                          )}
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
            <Pagination
              className="mt-5"
              layout="table"
              currentPage={currentPage}
              totalPages={totalPages}
              onPageChange={onPageChange}
              showIcons
            />
          </div>
        </div>
      </div>
    </div>
  );
}

export default TabelAbsen;
